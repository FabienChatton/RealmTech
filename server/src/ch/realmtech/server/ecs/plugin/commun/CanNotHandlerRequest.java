package ch.realmtech.server.ecs.plugin.commun;

public class CanNotHandlerRequest extends Exception {
    public CanNotHandlerRequest() {
    }

    public CanNotHandlerRequest(String message) {
        super(message);
    }

    public CanNotHandlerRequest(String message, Throwable cause) {
        super(message, cause);
    }

    public CanNotHandlerRequest(Throwable cause) {
        super(cause);
    }
}
