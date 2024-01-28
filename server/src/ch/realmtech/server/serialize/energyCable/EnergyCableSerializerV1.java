package ch.realmtech.server.serialize.energyCable;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.energy.EnergyCableEditEntity;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class EnergyCableSerializerV1 implements Serializer<Integer, EnergyCableEditEntity> {
    private ComponentMapper<FaceComponent> mFace;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer energyCableToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, energyCableToSerialize));
        FaceComponent faceComponent = mFace.get(energyCableToSerialize);
        ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getFaceSerializerController(), faceComponent);
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public EnergyCableEditEntity fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());
        byte face = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getFaceSerializerController());
        return new EnergyCableEditEntity(face);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer energyCableToSerialize) {
        return serializerController.getApplicationBytesLength(serializerController.getFaceSerializerController(), mFace.get(energyCableToSerialize));
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
