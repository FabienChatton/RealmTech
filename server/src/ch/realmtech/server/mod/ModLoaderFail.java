package ch.realmtech.server.mod;

public class ModLoaderFail extends Exception {
    public ModLoaderFail(String message) {
        super(message);
    }

    public ModLoaderFail(String message, Throwable cause) {
        super(message, cause);
    }

    public ModLoaderFail(Throwable cause) {
        super(cause);
    }

    public ModLoaderFail(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
