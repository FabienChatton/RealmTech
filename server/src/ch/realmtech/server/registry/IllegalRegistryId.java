package ch.realmtech.server.registry;

public class IllegalRegistryId extends RuntimeException {
    public IllegalRegistryId(String s) {
        super(s);
    }
}
