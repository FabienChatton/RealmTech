package ch.realmtech.server.serialize.face;

import ch.realmtech.server.ecs.component.FaceComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class FaceSerializerV1 implements Serializer<FaceComponent, Byte> {
    private ComponentMapper<FaceComponent> mFace;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, FaceComponent faceToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, faceToSerialize));
        byte flags = faceToSerialize.getFlags();
        buffer.writeByte(flags);
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public Byte fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());
        byte flags = buffer.readByte();
        return flags;
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, FaceComponent faceToSerialize) {
        return Byte.BYTES;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
