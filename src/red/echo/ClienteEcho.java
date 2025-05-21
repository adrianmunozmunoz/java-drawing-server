package red.echo;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente para el servicio Echo.
 * Se conecta al servidor, envía mensajes y muestra la respuesta.
 */
public class ClienteEcho {
    private final Socket socket;
    private final BufferedReader entrada;
    private final PrintWriter salida;

    public ClienteEcho(String host, int puerto) throws IOException {
        // Uso de try-with-resources no es posible en el constructor porque queremos conservar el socket,
        // pero se cierran en el método close() cuando ya se terminó la comunicación.
        this.socket = new Socket(host, puerto);
        this.entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.salida = new PrintWriter(socket.getOutputStream(), true);
    }

    public void write(String mensaje) {
        salida.println(mensaje);
    }

    public String read() throws IOException {
        return entrada.readLine();
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar el cliente: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ClienteEcho cliente = new ClienteEcho("localhost", 12345);
            System.out.println("Escribe un mensaje para enviar al servidor (escribe 'exit' para salir):");

            while (true) {
                System.out.print("> ");
                String mensaje = scanner.nextLine();
                cliente.write(mensaje);

                if (mensaje.equalsIgnoreCase("exit")) {
                    break;
                }

                String respuesta = cliente.read();
                System.out.println("Servidor: " + respuesta);
            }
            cliente.close();
        } catch (IOException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        }
    }
}

