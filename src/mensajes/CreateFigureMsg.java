package mensajes;

public class CreateFigureMsg extends Msg {
    public CreateFigureMsg(int tag, String content) {
        super(CREATE_FIGURE, tag, content);
    }
}
