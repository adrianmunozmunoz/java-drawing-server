package red.nio;

import mensajes.Msg;
import org.json.JSONObject;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * ClienteNIO permite conectar con el servidor usando NIO.
 * Se realiza una negociación inicial para elegir entre protocolo BIN (binario)
 * o JSON. Luego se envían y reciben mensajes según el formato elegido.
 */
public class ClienteNIO {
    private final String host;
    private final int puerto;
    private SocketChannel socketChannel;
    private int tagCounter = 0;
    private boolean usarJson = false;

    public ClienteNIO(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    /**
     * Se conecta al servidor y negocia el protocolo.
     */
    public void conectar() throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress(host, puerto));
        System.out.println("Conectado al servidor");

        Scanner scanner = new Scanner(System.in);
        System.out.print("¿Qué protocolo deseas usar? (BIN/JSON): ");
        String eleccion = scanner.nextLine().trim().toUpperCase();

        if (eleccion.equalsIgnoreCase("JSON")) {
            usarJson = true;
            socketChannel.write(ByteBuffer.wrap("PROTOCOLO JSON".getBytes(StandardCharsets.UTF_8)));
            System.out.println("Se utilizará el protocolo JSON");
        } else {
            usarJson = false;
            socketChannel.write(ByteBuffer.wrap("PROTOCOLO BIN".getBytes(StandardCharsets.UTF_8)));
            System.out.println("Se utilizará el protocolo BIN");
        }
    }

    /**
     * Envía un mensaje al servidor en el formato elegido.
     */
    public void enviarMensaje(byte tipo, String contenido) throws IOException {
        if (usarJson) {
            JSONObject obj = new JSONObject();
            obj.put("kind", tipo);
            obj.put("tag", tagCounter);
            obj.put("content", contenido);
            socketChannel.write(ByteBuffer.wrap(obj.toString().getBytes(StandardCharsets.UTF_8)));
        } else {
            Msg msg = new Msg(tipo, tagCounter, contenido);
            socketChannel.write(ByteBuffer.wrap(msg.encode()));
        }
        tagCounter++;
    }

    /**
     * Recibe un mensaje del servidor, interpretándolo según el protocolo.
     */
    public String recibirMensaje() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // Nota: En producción se debe manejar la posibilidad de recibir datos parciales.
        socketChannel.read(buffer);
        buffer.flip();

        if (usarJson) {
            String json = StandardCharsets.UTF_8.decode(buffer).toString();
            JSONObject obj = new JSONObject(json);
            return obj.getString("content");
        } else {
            Msg respuesta = Msg.decode(buffer);
            return respuesta.getContent();
        }
    }

    public void cerrar() throws IOException {
        socketChannel.close();
    }

    public static void main(String[] args) throws IOException {
        ClienteNIO cliente = new ClienteNIO("localhost", 12345);
        cliente.conectar();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nOpciones:");
            System.out.println("1. Login");
            System.out.println("2. Crear dibujo");
            System.out.println("3. Borrar dibujo");
            System.out.println("4. Crear figura");
            System.out.println("5. Borrar figura");
            System.out.println("6. Mover figura");
            System.out.println("7. Obtener figuras");
            System.out.println("8. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            if (opcion.equals("8")) {
                cliente.cerrar();
                System.out.println("Conexión cerrada.");
                break;
            }

            byte tipo;
            switch (opcion) {
                case "1" -> tipo = Msg.LOGIN;
                case "2" -> tipo = Msg.CREATE_DRAWING;
                case "3" -> tipo = Msg.DELETE_DRAWING;
                case "4" -> tipo = Msg.CREATE_FIGURE;
                case "5" -> tipo = Msg.DELETE_FIGURE;
                case "6" -> tipo = Msg.MOVE_FIGURE;
                case "7" -> tipo = Msg.GET_FIGURES;
                default -> {
                    System.out.println("Opción no válida");
                    continue;
                }
            }

            System.out.print("Ingrese el contenido del mensaje: ");
            String contenido = scanner.nextLine();
            cliente.enviarMensaje(tipo, contenido);
            String respuesta = cliente.recibirMensaje();
            System.out.println("Respuesta del servidor: " + respuesta);
        }
    }
}