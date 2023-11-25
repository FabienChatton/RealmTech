package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.types.SerializedApplicationBytes;

public interface SerializerCoder<InputType, OutputType> {
    SerializedApplicationBytes encode(InputType toSerialize);
    OutputType decode(SerializedApplicationBytes applicationBytes);
}
