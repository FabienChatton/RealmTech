package ch.realmtech.server.serialize.chunk;

import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ChunkSerializerManagerController implements ChunkSerializerManager {
    private final static Serializer<InfChunkComponent, Integer> CHUNK_SERIALIZER_9 = new ChunkSerializerV9();

    public byte[] toBytesLatest(World world, SerializerController serializerController, InfChunkComponent infChunkComponent) {
        return getSerializerLatest().toBytes(world, serializerController, infChunkComponent);
    }

    public Integer fromBytes(World world, SerializerController serializerController, byte[] bytes) {
        byte[] versionBytes = new byte[Integer.BYTES];
        System.arraycopy(bytes, 0, versionBytes, 0, versionBytes.length);
        ByteBuffer byteBuffer = ByteBuffer.wrap(versionBytes);
        int version = byteBuffer.getInt();

        return switch (version) {
            case 9 -> CHUNK_SERIALIZER_9.fromBytes(world, serializerController, bytes);
            default -> throw new IllegalStateException("Unexpected value: " + version + ". Cette version n'est pas implémentée");
        };
    }

    @Override
    public Serializer<InfChunkComponent, Integer> getSerializerLatest() {
        return CHUNK_SERIALIZER_9;
    }

    @Override
    public byte[] getMagicNumbers() {
        return "chunkInv\0".getBytes(StandardCharsets.UTF_8);
    }
}
