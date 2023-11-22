package ch.realmtech.server.serialize.chunk;

import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

public class ChunkSerializerV9 implements Serializer<InfChunkComponent, Integer> {
    @Override
    public byte[] toBytes(World world, SerializerController serializerController, InfChunkComponent infChunkComponent) {
        return new byte[0];
    }

    @Override
    public Integer fromBytes(World world, SerializerController serializerController, byte[] bytes) {
        return 0;
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, InfChunkComponent infChunkComponent) {
        return 0;
    }
}
