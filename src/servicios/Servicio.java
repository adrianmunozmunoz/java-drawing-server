package servicios;

import mensajes.Msg;
import red.nio.ServidorNIO;
import java.nio.channels.SocketChannel;

/**
 * Interfaz que define un servicio que procesa un mensaje recibido en un canal.
 */
public interface Servicio {
    String procesar(Msg mensaje, SocketChannel canal, ServidorNIO servidor);
}
