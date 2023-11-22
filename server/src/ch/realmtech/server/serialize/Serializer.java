package ch.realmtech.server.serialize;

import com.artemis.World;

public interface Serializer<InputType, ReturnType> {
    byte[] toBytes(World world, SerializerController serializerController, InputType toSerialize);
    ReturnType fromBytes(World world, SerializerController serializerController, byte[] bytes);
    int getBytesSize(World world, SerializerController serializerController, InputType toSerialize);
}
