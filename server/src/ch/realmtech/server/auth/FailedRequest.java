package ch.realmtech.server.auth;

public class FailedRequest extends Exception {
    public FailedRequest(int statusCode, String message) {
        super(statusCode + ", " + message);
    }
}
