package mensajes;

public class DeleteFigureMsg extends Msg {
    public DeleteFigureMsg(int tag, String content) {
        super(DELETE_FIGURE, tag, content);
    }
}
