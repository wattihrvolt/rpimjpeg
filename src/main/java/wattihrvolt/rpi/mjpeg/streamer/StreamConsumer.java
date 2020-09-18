package wattihrvolt.rpi.mjpeg.streamer;

import java.io.OutputStream;

public class StreamConsumer {

    public enum Type {
        MULTIPART,
        SNAPSHOT,
        STREAM
    };

    private final Type type;
    private OutputStream os;

    public StreamConsumer(OutputStream os, Type type) {
        this.os = os;
        this.type = type;
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public Type getType() {
        return type;
    }


}
