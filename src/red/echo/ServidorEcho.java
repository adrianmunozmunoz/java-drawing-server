package red.echo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServidorEcho {
    private static final int PUERTO = 12345;
    private ServerSocket serverSocket;
    private final AtomicBoolean running = new AtomicBoolean(true);

    /**
     * Inicia el servidor Echo, aceptando conexiones y creando un hilo por cada cliente.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor Echo en ejecución en el puerto " + PUERTO);

            while (running.get()) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Cliente conectado desde: " + socket.getInetAddress());
                    new Thread(new ManejadorCliente(socket)).start();
                } catch (IOException e) {
                    if (!running.get()) {
                        System.out.println("Servidor detenido.");
                        break;
                    }
                    System.err.println("Error al aceptar conexión: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    /**
     * Detiene el servidor y cierra el ServerSocket.
     */
    public void close() {
        running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ServidorEcho servidor = new ServidorEcho();
        servidor.start();
    }
}


