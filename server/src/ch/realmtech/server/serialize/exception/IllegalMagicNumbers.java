package ch.realmtech.server.serialize.exception;

public class IllegalMagicNumbers extends RuntimeException {
    public IllegalMagicNumbers() {
    }

    public IllegalMagicNumbers(String message) {
        super(message);
    }

    public IllegalMagicNumbers(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalMagicNumbers(Throwable cause) {
        super(cause);
    }

    public IllegalMagicNumbers(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
