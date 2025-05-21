package servicios;

import mensajes.Msg;
import red.nio.ServidorNIO;

import java.nio.channels.SocketChannel;

public class CreateDrawingServicio implements Servicio {
    @Override
    public String procesar(Msg mensaje, SocketChannel canal, ServidorNIO servidor) {
        return servidor.crearDibujo(mensaje.getContent(), canal);
    }
}