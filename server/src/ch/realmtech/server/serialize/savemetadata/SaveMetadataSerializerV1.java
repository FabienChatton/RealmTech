package ch.realmtech.server.serialize.savemetadata;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.SaveMetadataComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class SaveMetadataSerializerV1 implements Serializer<Integer, Integer> {
    private ComponentMapper<SaveMetadataComponent> mMetaData;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer metadataToSerialize) {
        SaveMetadataComponent saveMetadataComponent = mMetaData.get(metadataToSerialize);
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, metadataToSerialize));

        ByteBufferHelper.writeString(buffer, saveMetadataComponent.saveName);
        buffer.writeLong(saveMetadataComponent.seed);
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public Integer fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());
        String saveName = ByteBufferHelper.getString(buffer);
        long seed = buffer.readLong();

        int saveMetadataId = world.create();
        world.edit(saveMetadataId).create(SaveMetadataComponent.class).set(seed, saveName, world);

        return saveMetadataId;
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer toSerialize) {
        SaveMetadataComponent saveMetadataComponent = mMetaData.get(toSerialize);
        int saveNameLength = saveMetadataComponent.saveName.length() + 1;
        int seedLength = Long.BYTES;
        return saveNameLength + seedLength;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
