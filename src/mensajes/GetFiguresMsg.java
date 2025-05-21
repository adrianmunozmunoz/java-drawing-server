package mensajes;

public class GetFiguresMsg extends Msg {
    public GetFiguresMsg(int tag, String content) {
        super(GET_FIGURES, tag, content);
    }
}
