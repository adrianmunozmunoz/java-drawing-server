package test.nio;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import red.nio.*;
import static org.junit.jupiter.api.Assertions.*;

import mensajes.*;

class ServidorNIOTest {
    private static ExecutorService executor;
    private static ServidorNIO servidor;

    @BeforeAll
    static void iniciarServidor() {
        executor = Executors.newSingleThreadExecutor();
        servidor = new ServidorNIO();
        executor.submit(() -> {
            try {
                servidor.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterAll
    static void detenerServidor() {
        executor.shutdownNow();
    }

    @Test
    @Order(1)
    void testLogin() throws IOException {
        ClienteNIO cliente = new ClienteNIO("localhost", 12345);
        cliente.conectar();
        cliente.enviarMensaje(Msg.LOGIN, "admin");
        assertEquals("Usuario logueado: admin", cliente.recibirMensaje());
        cliente.cerrar();
    }

    @Test
    @Order(2)
    void testCrearDibujo() throws IOException {
        ClienteNIO cliente = new ClienteNIO("localhost", 12345);
        cliente.conectar();
        cliente.enviarMensaje(Msg.LOGIN, "admin");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.CREATE_DRAWING, "Dibujo1");
        assertEquals("Dibujo creado: Dibujo1", cliente.recibirMensaje());
        cliente.cerrar();
    }

    @Test
    @Order(3)
    void testCrearFigura() throws IOException {
        ClienteNIO cliente = new ClienteNIO("localhost", 12345);
        cliente.conectar();
        cliente.enviarMensaje(Msg.LOGIN, "admin");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.CREATE_DRAWING, "Dibujo1");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.CREATE_FIGURE, "Dibujo1 Circulo 50 50 20");
        assertEquals("Figura agregada a Dibujo1", cliente.recibirMensaje());
        cliente.cerrar();
    }

    @Test
    @Order(4)
    void testMoverFigura() throws IOException {
        ClienteNIO cliente = new ClienteNIO("localhost", 12345);
        cliente.conectar();
        cliente.enviarMensaje(Msg.LOGIN, "admin");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.CREATE_DRAWING, "Dibujo1");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.CREATE_FIGURE, "Dibujo1 Circulo 50 50 20");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.MOVE_FIGURE, "Dibujo1 1 10 15");
        assertEquals("Figura movida en Dibujo1", cliente.recibirMensaje());
        cliente.cerrar();
    }

    @Test
    @Order(5)
    void testBorrarFigura() throws IOException {
        ClienteNIO cliente = new ClienteNIO("localhost", 12345);
        cliente.conectar();
        cliente.enviarMensaje(Msg.LOGIN, "admin");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.CREATE_DRAWING, "Dibujo1");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.CREATE_FIGURE, "Dibujo1 Circulo 50 50 20");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.DELETE_FIGURE, "Dibujo1 1");
        assertEquals("Figura eliminada de Dibujo1", cliente.recibirMensaje());
        cliente.cerrar();
    }

    @Test
    @Order(6)
    void testBorrarDibujo() throws IOException {
        ClienteNIO cliente = new ClienteNIO("localhost", 12345);
        cliente.conectar();
        cliente.enviarMensaje(Msg.LOGIN, "admin");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.CREATE_DRAWING, "Dibujo1");
        cliente.recibirMensaje();
        cliente.enviarMensaje(Msg.DELETE_DRAWING, "Dibujo1");
        assertEquals("Dibujo eliminado: Dibujo1", cliente.recibirMensaje());
        cliente.cerrar();
    }
}