package ch.realmtech.server.serialize.uuid;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.World;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UuidSerializerV1 implements Serializer<UUID, UUID> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, UUID uuidToSerialize) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(getBytesSize(world, serializerController, uuidToSerialize));
        ByteBufferHelper.writeUUID(byteBuffer, uuidToSerialize);
        return new SerializedRawBytes(byteBuffer.array());
    }

    @Override
    public UUID fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(rawBytes.rawBytes());
        return ByteBufferHelper.readUUID(byteBuffer);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, UUID toSerialize) {
        return Long.BYTES * 2;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
