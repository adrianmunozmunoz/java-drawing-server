package servicios;

import mensajes.Msg;
import red.nio.ServidorNIO;

import java.nio.channels.SocketChannel;

public class CreateFigureServicio implements Servicio {
    @Override
    public String procesar(Msg mensaje, SocketChannel canal, ServidorNIO servidor) {
        return servidor.crearFigura(mensaje.getContent(), canal);
    }
}
