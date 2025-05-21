package mensajes;

public class MsgFactory {
    public static Msg createMessage(byte tipo, int tag, String contenido) {
        return switch (tipo) {
            case Msg.LOGIN -> new LoginMsg(tag, contenido);
            case Msg.CREATE_DRAWING -> new CreateDrawingMsg(tag, contenido);
            case Msg.DELETE_DRAWING -> new DeleteDrawingMsg(tag, contenido);
            case Msg.CREATE_FIGURE -> new CreateFigureMsg(tag, contenido);
            case Msg.DELETE_FIGURE -> new DeleteFigureMsg(tag, contenido);
            case Msg.MOVE_FIGURE -> new MoveFigureMsg(tag, contenido);
            case Msg.GET_FIGURES -> new GetFiguresMsg(tag, contenido);
            case Msg.RESPONSE -> new Msg(Msg.RESPONSE, tag, contenido);
            default -> throw new IllegalArgumentException("Tipo de mensaje desconocido: " + tipo);
        };
    }
}
