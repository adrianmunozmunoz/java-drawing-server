package mensajes;

public class DeleteDrawingMsg extends Msg {
    public DeleteDrawingMsg(int tag, String content) {
        super(DELETE_DRAWING, tag, content);
    }
}
