package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.registery.CellRegisterEntry;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class CellSerializerV3 implements Serializer<Integer, CellArgs> {
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<ChestComponent> mChest;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<FurnaceComponent> mFurnace;
    private ComponentMapper<FaceComponent> mFace;
    private ComponentMapper<EnergyBatteryComponent> mEnergyBattery;
    @Override
    public SerializedRawBytes toRawBytes(World world, SerializerController serializerController, Integer cellToSerialize) {
        CellComponent cellComponentToSerialize = mCell.get(cellToSerialize);
        ByteBuf buffer = Unpooled.buffer(getBytesSize(world, serializerController, cellToSerialize));
        int hashCellRegisterEntry = CellRegisterEntry.getHash(cellComponentToSerialize.cellRegisterEntry);
        byte innerChunkPos = Cells.getInnerChunkPos(cellComponentToSerialize.getInnerPosX(), cellComponentToSerialize.getInnerPosY());

        buffer.writeInt(hashCellRegisterEntry);
        buffer.writeByte(innerChunkPos);

        byte paddingId = 0;
        Runnable writePadding = null;
        if (mChest.has(cellToSerialize)) {
            paddingId = 1;
            writePadding = () -> ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getChestSerializerController(), cellToSerialize);
        }

        if (mCraftingTable.has(cellToSerialize)) {
            paddingId = 2;
            writePadding = () -> ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getCraftingTableController(), cellToSerialize);
        }

        if (mFurnace.has(cellToSerialize)) {
            paddingId = 3;
            writePadding = () -> ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getFurnaceSerializerController(), cellToSerialize);
        }

        if (mEnergyBattery.has(cellToSerialize)) {
            paddingId = 4;
            writePadding = () -> ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getEnergyBatterySerializerController(), cellToSerialize);
        }

        buffer.writeByte(paddingId);
        if (paddingId != 0) {
            writePadding.run();
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
            editEntityArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getChestSerializerController());
        }
        if (paddingId == 2) {
            editEntityArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getCraftingTableController());
        }
        if (paddingId == 3) {
            editEntityArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getFurnaceSerializerController());
        }
        if (paddingId == 4) {
            editEntityArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getEnergyBatterySerializerController());
        }
        return new CellArgs(CellRegisterEntry.getCellModAndCellHash(hashCellRegisterEntry), innerChunkPos, editEntityArgs);
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer cellToSerialize) {
        int hashCell = Integer.BYTES;
        int pos = Byte.BYTES;
        byte paddingId = Byte.BYTES;

        int paddingLength = 0;
        if (mCraftingTable.has(cellToSerialize)) {
            paddingLength = serializerController.getApplicationBytesLength(serializerController.getCraftingTableController(), cellToSerialize);
        }

        return hashCell + pos + paddingId + paddingLength;
    }

    @Override
    public byte getVersion() {
        return 3;
    }
}