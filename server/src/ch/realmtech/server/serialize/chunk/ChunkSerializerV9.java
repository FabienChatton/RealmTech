package ch.realmtech.server.serialize.chunk;

import ch.realmtech.server.ecs.component.InfCellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ChunkSerializerV9 implements Serializer<InfChunkComponent, Integer> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, InfChunkComponent chunkToSerialize) {
        ComponentMapper<InfCellComponent> mCell = world.getMapper(InfCellComponent.class);
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, chunkToSerialize));
        buffer.writeShort(chunkToSerialize.infCellsId.length);
        for (int i = 0; i < chunkToSerialize.infCellsId.length; i++) {
            int cellId = chunkToSerialize.infCellsId[i];
            InfCellComponent infCellComponent = mCell.get(cellId);
        }
        return new SerializedRawBytes(buffer.array());
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
    public byte getVersion() {
        return 9;
    }
}
