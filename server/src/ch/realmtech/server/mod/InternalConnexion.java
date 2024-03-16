package ch.realmtech.server.mod;

public interface InternalConnexion extends ClientContext {
    void closeEcs();
    void displayErrorPopup(String message);
}
