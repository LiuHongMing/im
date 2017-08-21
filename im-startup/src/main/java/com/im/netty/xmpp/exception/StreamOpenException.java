package com.im.netty.xmpp.exception;

public class StreamOpenException extends Exception {

    public StreamOpenException() {
        super();
    }

    public StreamOpenException(String message) {
        super(message);
    }

    public StreamOpenException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamOpenException(Throwable cause) {
        super(cause);
    }
}
