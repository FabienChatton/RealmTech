package ch.realmtech.server.serialize.chunk;

import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.World;

public class ChunkSerializerV9 implements Serializer<InfChunkComponent, Integer> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, InfChunkComponent toSerialize) {
        return null;
    }

    @Override
    public Integer fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        return null;
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, InfChunkComponent toSerialize) {
        return 0;
    }

    @Override
    public int getVersion() {
        return 9;
    }
}
