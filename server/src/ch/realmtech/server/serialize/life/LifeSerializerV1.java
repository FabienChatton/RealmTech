package ch.realmtech.server.serialize.life;

import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class LifeSerializerV1 implements Serializer<LifeComponent, LifeArgs> {

    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, LifeComponent lifeToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, lifeToSerialize));

        buffer.writeInt(lifeToSerialize.getHeart());

        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public LifeArgs fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        int heart = buffer.readInt();

        return new LifeArgs(heart);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, LifeComponent toSerialize) {
        return Integer.BYTES;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
