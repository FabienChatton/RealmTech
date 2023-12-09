package ch.realmtech.server.serialize.furnace;

import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.World;

import java.util.function.Consumer;

public class FurnaceSerializerV1 implements Serializer<Integer, Consumer<Integer>> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer toSerialize) {
        return null;
    }

    @Override
    public Consumer<Integer> fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        return null;
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer toSerialize) {
        return 0;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
