package red.nio;

import dibujos.Dibujo;
import figuras.Figura;
import mensajes.*;
import parsing.FiguraParser;
import servicios.*;
import usuarios.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Servidor NIO que maneja clientes con protocolos BIN o JSON,
 * delegando el procesamiento de mensajes a servicios.
 */
public class ServidorNIO {
    private static final int PUERTO = 12345;
    private Selector selector;
    private final Map<SocketChannel, Usuario> usuariosConectados = new HashMap<>();
    private final Map<String, Dibujo> dibujos = new HashMap<>();

    // Mapa de servicios para delegar el procesamiento de mensajes.
    private final Map<Byte, Servicio> servicios = new HashMap<>();
    // Mapa para almacenar el protocolo negociado por cada cliente.
    private final Map<SocketChannel, String> protocolosPorCliente = new HashMap<>();

    public void start() throws IOException {
        // Inicialización del servidor
        selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(PUERTO));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Servidor NIO iniciado en el puerto " + PUERTO);

        // Registro de servicios disponibles
        servicios.put(Msg.LOGIN, new LoginServicio());
        servicios.put(Msg.CREATE_DRAWING, new CreateDrawingServicio());
        servicios.put(Msg.DELETE_DRAWING, new DeleteDrawingServicio());
        servicios.put(Msg.CREATE_FIGURE, new CreateFigureServicio());
        servicios.put(Msg.DELETE_FIGURE, new DeleteFigureServicio());
        servicios.put(Msg.MOVE_FIGURE, new MoveFigureServicio());
        servicios.put(Msg.GET_FIGURES, new GetFiguresServicio());

        // Bucle principal del servidor
        while (true) {
            selector.select();
            Iterator<SelectionKey> keysIt = selector.selectedKeys().iterator();
            while (keysIt.hasNext()) {
                SelectionKey key = keysIt.next();
                keysIt.remove();
                try {
                    if (key.isAcceptable()) {
                        aceptarConexion(serverSocket);
                    } else if (key.isReadable()) {
                        manejarLectura(key);
                    }
                } catch (IOException e) {
                    // En caso de error, se cierra el canal afectado
                    key.channel().close();
                }
            }
        }
    }

    private void aceptarConexion(ServerSocketChannel serverSocket) throws IOException {
        SocketChannel socketChannel = serverSocket.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(4096));
        System.out.println("Cliente conectado: " + socketChannel.getRemoteAddress());
    }

    /**
     * Método auxiliar para decodificar mensajes en JSON.
     */
    private Msg decodificarDesdeJSON(String json) {
        try {
            org.json.JSONObject obj = new org.json.JSONObject(json);
            byte kind = (byte) obj.getInt("kind");
            int tag = obj.getInt("tag");
            String content = obj.getString("content");
            return MsgFactory.createMessage(kind, tag, content);
        } catch (Exception e) {
            return new Msg(Msg.RESPONSE, 0, "Error: JSON mal formado");
        }
    }

    /**
     * Maneja la lectura de datos de un cliente.
     * Primero se negocia el protocolo (si no se ha definido aún).
     * Luego se decodifica el mensaje según el protocolo y se procesa.
     */
    private void manejarLectura(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        int bytesRead = socketChannel.read(buffer);

        if (bytesRead == -1) {
            socketChannel.close();
            usuariosConectados.remove(socketChannel);
            protocolosPorCliente.remove(socketChannel);
            return;
        }

        buffer.flip();
        String protocolo = protocolosPorCliente.get(socketChannel);

        // Si aún no se ha negociado el protocolo, se interpreta el primer mensaje como la elección.
        if (protocolo == null) {
            String decision = StandardCharsets.UTF_8.decode(buffer).toString().trim();
            if (decision.equalsIgnoreCase("PROTOCOLO JSON")) {
                protocolosPorCliente.put(socketChannel, "JSON");
            } else {
                protocolosPorCliente.put(socketChannel, "BIN");
            }
            buffer.clear();
            return;
        }

        // Decodificación del mensaje según el protocolo negociado
        Msg mensaje;
        if (protocolo.equals("JSON")) {
            String json = StandardCharsets.UTF_8.decode(buffer).toString();
            mensaje = decodificarDesdeJSON(json);
        } else {
            mensaje = Msg.decode(buffer);
        }

        buffer.clear();
        System.out.println("Mensaje recibido: " + mensaje.getKind() + " - " + mensaje.getContent());

        // Procesa el mensaje delegando al servicio correspondiente
        String respuesta = Optional.ofNullable(procesarMensaje(mensaje, socketChannel))
                .orElse("Error interno del servidor");
        enviarRespuesta(socketChannel, mensaje.getTag(), respuesta);
    }

    /**
     * Envía la respuesta al cliente, en el formato correspondiente.
     */
    private void enviarRespuesta(SocketChannel socketChannel, int tag, String respuesta) {
        try {
            String protocolo = protocolosPorCliente.get(socketChannel);
            if ("JSON".equals(protocolo)) {
                // Construir el objeto JSON de forma segura, escapando caracteres especiales.
                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put("kind", Msg.RESPONSE);
                obj.put("tag", tag);
                obj.put("content", respuesta);
                String json = obj.toString();
                socketChannel.write(ByteBuffer.wrap(json.getBytes(StandardCharsets.UTF_8)));
            } else {
                Msg msgRespuesta = new Msg(Msg.RESPONSE, tag, respuesta);
                socketChannel.write(ByteBuffer.wrap(msgRespuesta.encode()));
            }
        } catch (IOException e) {
            System.err.println("Error enviando respuesta al cliente: " + e.getMessage());
        }
    }


    /**
     * Procesa el mensaje recibido delegando a un servicio según su tipo.
     */
    private String procesarMensaje(Msg mensaje, SocketChannel socketChannel) {
        try {
            Servicio servicio = servicios.get(mensaje.getKind());
            if (servicio != null) {
                return servicio.procesar(mensaje, socketChannel, this);
            } else {
                return "Error: Tipo de mensaje desconocido";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Métodos utilizados por los servicios
    public String autenticarUsuario(String loginInfo, SocketChannel socketChannel) {
        // Se espera el formato: "<username> <role>"
        String[] partes = loginInfo.split("\\s+");
        if (partes.length != 2) {
            return "Error: Formato esperado: <username> <role> (por ejemplo: juan admin)";
        }
        String username = partes[0];
        String role = partes[1].toLowerCase();

        if (!role.equals("admin") && !role.equals("user")) {
            return "Error: El rol debe ser 'admin' o 'user'. Formato esperado: <username> <role> (por ejemplo: juan admin)";
        }

        Usuario usuario;
        if (role.equals("admin")) {
            usuario = new Administrador(username, "password");
        } else {
            usuario = new Usuario(username, "password");
        }
        usuariosConectados.put(socketChannel, usuario);
        return "Usuario logueado: " + username + " (" + role + ")";
    }


    public String crearDibujo(String nombreDibujo, SocketChannel socketChannel) {
        Usuario usuario = usuariosConectados.get(socketChannel);
        if (!(usuario instanceof Administrador)) {
            return "Error: Solo un administrador puede crear dibujos.";
        }
        dibujos.put(nombreDibujo, new Dibujo(usuario));
        return "Dibujo creado: " + nombreDibujo;
    }

    public String borrarDibujo(String nombreDibujo, SocketChannel socketChannel) {
        Usuario usuario = usuariosConectados.get(socketChannel);
        if (!(usuario instanceof Administrador)) {
            return "Error: Solo un administrador puede borrar dibujos.";
        }
        return dibujos.remove(nombreDibujo) != null
                ? "Dibujo eliminado: " + nombreDibujo
                : "Error: Dibujo no encontrado";
    }

    public String crearFigura(String contenido, SocketChannel socketChannel) {
        Usuario usuario = usuariosConectados.get(socketChannel);
        if (usuario == null) return "Error: Usuario no autenticado";

        String[] datos = contenido.split(" ", 2);
        if (datos.length < 2) return "Error: Formato de mensaje inválido";

        Dibujo dibujo = dibujos.get(datos[0]);
        if (dibujo == null) return "Error: Dibujo no encontrado";

        try {
            Figura figura = FiguraParser.parse(datos[1]);
            return dibujo.agregarFigura(figura, usuario)
                    ? "Figura agregada a " + datos[0]
                    : "Error: No se pudo agregar la figura.";
        } catch (IllegalArgumentException e) {
            return "Error: Tipo de figura no válido. Formato esperado: <nombreDibujo> <tipoFigura (primera letra en mayucula)> <parametros>\"";
        }
    }

    public String borrarFigura(String contenido, SocketChannel socketChannel) {
        Usuario usuario = usuariosConectados.get(socketChannel);
        if (usuario == null) return "Error: Usuario no autenticado";

        String[] datos = contenido.split(" ");
        if (datos.length < 2) return "Error: Formato de mensaje inválido";

        Dibujo dibujo = dibujos.get(datos[0]);
        if (dibujo == null) return "Error: Dibujo no encontrado";

        try {
            int idFigura = Integer.parseInt(datos[1]);
            return dibujo.eliminarFigura(idFigura, usuario)
                    ? "Figura eliminada de " + datos[0]
                    : "Error: No se pudo eliminar la figura.";
        } catch (NumberFormatException e) {
            return "Error: Formato de ID de figura inválido. Formato esperado: <nombreDibujo> <idFigura>";
        }
    }

    public String moverFigura(String contenido, SocketChannel socketChannel) {
        Usuario usuario = usuariosConectados.get(socketChannel);
        if (usuario == null) return "Error: Usuario no autenticado";

        String[] datos = contenido.split(" ");
        if (datos.length < 4) return "Error: Formato de mensaje inválido";

        Dibujo dibujo = dibujos.get(datos[0]);
        if (dibujo == null) return "Error: Dibujo no encontrado";

        try {
            int idFigura = Integer.parseInt(datos[1]);
            int dx = Integer.parseInt(datos[2]);
            int dy = Integer.parseInt(datos[3]);
            return dibujo.moverFigura(idFigura, dx, dy, usuario)
                    ? "Figura movida en " + datos[0]
                    : "Error: No se pudo mover la figura.";
        } catch (NumberFormatException e) {
            return "Error: Formato de coordenadas/ID inválido Formato esperado: <nombreDibujo> <idFigura> <dx> <dy>";
        }
    }

    public String obtenerFiguras(String nombreDibujo, SocketChannel socketChannel) {
        Usuario usuario = usuariosConectados.get(socketChannel);
        if (usuario == null) return "Error: Usuario no autenticado";

        Dibujo dibujo = dibujos.get(nombreDibujo);
        return (dibujo != null)
                ? dibujo.obtenerDescripcion(usuario)
                : "Error: Dibujo no encontradoFormato esperado: <nombreDibujo>";
    }

    public static void main(String[] args) throws IOException {
        new ServidorNIO().start();
    }
}