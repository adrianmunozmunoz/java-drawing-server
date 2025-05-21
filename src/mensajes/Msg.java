package mensajes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class Msg {
    public static final byte LOGIN = 1;
    public static final byte CREATE_DRAWING = 2;
    public static final byte DELETE_DRAWING = 3;
    public static final byte CREATE_FIGURE = 4;
    public static final byte DELETE_FIGURE = 5;
    public static final byte MOVE_FIGURE = 6;
    public static final byte GET_FIGURES = 7;
    public static final byte RESPONSE = 100;

    protected final byte kind;
    protected final int tag;
    protected final String content;

    public Msg(byte kind, int tag, String content) {
        this.kind = kind;
        this.tag = tag;
        this.content = content;
    }

    public Msg(byte kind, String content) {
        this(kind, (int) (Math.random() * Integer.MAX_VALUE), content);
    }

    public byte getKind() {
        return kind;
    }

    public int getTag() {
        return tag;
    }

    public String getContent() {
        return content;
    }

    public byte[] encode() {
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        int length = 1 + 4 + 4 + contentBytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(kind);
        buffer.putInt(tag);
        buffer.putInt(contentBytes.length);
        buffer.put(contentBytes);
        return buffer.array();
    }

    public static Msg decode(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        byte kind = buffer.get();
        int tag = buffer.getInt();
        int length = buffer.getInt();
        byte[] contentBytes = new byte[length];
        buffer.get(contentBytes);
        String content = new String(contentBytes, StandardCharsets.UTF_8);
        return MsgFactory.createMessage(kind, tag, content);
    }
}

