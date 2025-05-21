package red.echo;

import java.io.*;
import java.net.*;

public class ManejadorCliente implements Runnable {
    private final Socket socket;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                // Se podría usar un logger en lugar de System.out.println.
                System.out.println("Mensaje recibido: " + mensaje);
                salida.println("Echo: " + mensaje);

                if (mensaje.equalsIgnoreCase("exit")) {
                    System.out.println("Cliente desconectado.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error en la comunicación con el cliente: " + e.getMessage());
        } finally {
            cerrarSocket();
        }
    }

    private void cerrarSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el socket: " + e.getMessage());
        }
    }
}