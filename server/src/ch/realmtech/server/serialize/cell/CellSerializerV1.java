package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.registery.CellRegisterEntry;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CellSerializerV1 implements Serializer<Integer, CellArgs> {
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer cellToSerialize) {
        ComponentMapper<CellComponent> mCell = world.getMapper(CellComponent.class);
        CellComponent cellComponentToSerialize = mCell.get(cellToSerialize);
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, cellToSerialize));
        int hashCellRegisterEntry = CellRegisterEntry.getHash(cellComponentToSerialize.cellRegisterEntry);
        byte innerChunkPos = Cells.getInnerChunkPos(cellComponentToSerialize.getInnerPosX(), cellComponentToSerialize.getInnerPosY());

        buffer.writeInt(hashCellRegisterEntry);
        buffer.writeByte(innerChunkPos);
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public CellArgs fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        int hashCellRegisterEntry = buffer.readInt();
        byte innerChunkPos = buffer.readByte();
        return new CellArgs(CellRegisterEntry.getCellModAndCellHash(hashCellRegisterEntry), innerChunkPos);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer toSerialize) {
        return Byte.BYTES + Integer.BYTES;
    }

    @Override
    public byte getVersion() {
        return 1;
    }
}
