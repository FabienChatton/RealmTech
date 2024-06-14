package ch.realmtech.server.serialize.notify;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.divers.Notify;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NotifySerializerV1 implements Serializer<Notify, Notify> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Notify notifyToSerialize) {
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, notifyToSerialize));
        ByteBufferHelper.writeString(buffer, notifyToSerialize.title());
        ByteBufferHelper.writeString(buffer, notifyToSerialize.message());
        buffer.writeInt(notifyToSerialize.secondeToShow());
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public Notify fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());
        String title = ByteBufferHelper.getString(buffer);
        String message = ByteBufferHelper.getString(buffer);
        int secondeToShow = buffer.readInt();
        return new Notify(title, message, secondeToShow);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Notify notifyToSerialize) {
        int titleLength = notifyToSerialize.title().length() + 1;
        int messageLength = notifyToSerialize.message().length() + 1;
        int timeToShowLength = Integer.BYTES;
        return titleLength + messageLength + timeToShowLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
