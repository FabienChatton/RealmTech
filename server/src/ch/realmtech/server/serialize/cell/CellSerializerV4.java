package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.registery.CellRegisterEntry;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CellSerializerV4 implements Serializer<Integer, CellArgs> {
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<ChestComponent> mChest;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<FurnaceComponent> mFurnace;
    private ComponentMapper<FaceComponent> mFace;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    private ComponentMapper<CellPaddingSerializableEditEntity> mCellPadding;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer cellToSerialize) {
        CellComponent cellComponentToSerialize = mCell.get(cellToSerialize);
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, cellToSerialize));
        int hashCellRegisterEntry = CellRegisterEntry.getHash(cellComponentToSerialize.cellRegisterEntry);
        byte innerChunkPos = Cells.getInnerChunkPos(cellComponentToSerialize.getInnerPosX(), cellComponentToSerialize.getInnerPosY());

        buffer.writeInt(hashCellRegisterEntry);
        buffer.writeByte(innerChunkPos);

        if (mCellPadding.has(cellToSerialize)) {
            buffer.writeByte(1);
            SerializedApplicationBytes encode = mCellPadding.get(cellToSerialize).getSerializerController().encode(cellToSerialize);
            ByteBufferHelper.writeSerializedApplicationBytes(buffer, encode);
        } else {
            buffer.writeByte(0);
        }
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public CellArgs fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        int hashCellRegisterEntry = buffer.readInt();
        byte innerChunkPos = buffer.readByte();
        byte paddingId = buffer.readByte();
        EditEntity editEntityArgs = null;
        if (paddingId == 1) {
            editEntityArgs = ByteBufferHelper.decodeSerializedApplicationBytesByMagic(buffer, serializerController);
        }
        return new CellArgs(CellRegisterEntry.getCellModAndCellHash(hashCellRegisterEntry), innerChunkPos, editEntityArgs);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer cellToSerialize) {
        int hashCell = Integer.BYTES;
        int pos = Byte.BYTES;
        byte paddingId = Byte.BYTES;

        int paddingLength;
        if (mCellPadding.has(cellToSerialize)) {
            paddingLength = serializerController.getApplicationBytesLength(mCellPadding.get(cellToSerialize).getSerializerController(), cellToSerialize);
        } else {
            paddingLength = 0;
        }

        return hashCell + pos + paddingId + paddingLength;
    }

    @Override
    public byte getVersion() {
        return 4;
    }
}
