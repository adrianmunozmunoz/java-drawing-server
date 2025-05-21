package test.echo;

import org.junit.jupiter.api.*;
import red.echo.ServidorEcho;

import java.io.*;
import java.net.*;
import static org.junit.jupiter.api.Assertions.*;


class ServidorEchoTest {
    private static Thread servidorThread;

    @BeforeAll
    static void iniciarServidor() {
        servidorThread = new Thread(() -> ServidorEcho.main(new String[]{}));
        servidorThread.start();

        // Esperar un poco a que el servidor inicie completamente
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restaurar la interrupción
            System.err.println("Interrupción mientras se iniciaba el servidor.");
        }
    }

    @AfterAll
    static void detenerServidor() {
        servidorThread.interrupt();  // Detiene el servidor después de la prueba
    }

    @Test
    void testEchoServidor() throws IOException {
        try (
                Socket socket = new Socket("localhost", 12345);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String mensaje = "Hola Servidor";
            salida.println(mensaje);
            String respuesta = entrada.readLine();

            assertNotNull(respuesta, "El servidor no respondió.");
            assertEquals("Echo: " + mensaje, respuesta, "La respuesta del servidor no es la esperada.");
        }
    }
}
