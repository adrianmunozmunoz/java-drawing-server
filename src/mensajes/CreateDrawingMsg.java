package mensajes;

public class CreateDrawingMsg extends Msg {
    public CreateDrawingMsg(int tag, String content) {
        super(CREATE_DRAWING, tag, content);
    }
}
