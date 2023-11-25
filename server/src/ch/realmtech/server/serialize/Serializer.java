package ch.realmtech.server.serialize;

import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.World;

public interface Serializer<InputType, OutputType> {
    SerializedRawBytes toRawBytes(World world, SerializerController serializerController, InputType toSerialize);
    OutputType fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes);
    int getBytesSize(World world, SerializerController serializerController, InputType toSerialize);
    byte getVersion();
}
