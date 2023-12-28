package ch.realmtech.server.serialize.exception;

public class TooLongBytes extends RuntimeException {
    public TooLongBytes() {
    }

    public TooLongBytes(String message) {
        super(message);
    }

    public TooLongBytes(String message, Throwable cause) {
        super(message, cause);
    }

    public TooLongBytes(Throwable cause) {
        super(cause);
    }

    public TooLongBytes(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
