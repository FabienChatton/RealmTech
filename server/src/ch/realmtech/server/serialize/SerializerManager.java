package ch.realmtech.server.serialize;

import com.artemis.World;

public interface SerializerManager<InputType, OutputType> {

    byte[] toBytesLatest(World world, SerializerController serializerController, InputType toSerialize);

    OutputType fromBytes(World world, SerializerController serializerController, byte[] bytes);

    default int getBytesSize(World world, SerializerController serializerController, InputType toSerialize) {
        return getSerializerLatest().getBytesSize(world, serializerController, toSerialize);
    }

    Serializer<InputType, OutputType> getSerializerLatest();

    byte[] getMagicNumbers();
}
