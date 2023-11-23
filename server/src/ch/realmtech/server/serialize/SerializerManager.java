package ch.realmtech.server.serialize;

public interface SerializerManager<InputType, OutputType> {
    byte[] getMagicNumbers();
    Serializer<InputType, OutputType> getSerializer(int version);
    Serializer<InputType, OutputType> getSerializer();
}
