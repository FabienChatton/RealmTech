package ch.realmtech.server.serialize.types;

public record SerializedApplicationBytes(byte[] applicationBytes) {

    public int getLength() {
        return applicationBytes.length;
    }
}
