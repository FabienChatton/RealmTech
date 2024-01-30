package ch.realmtech.server.serialize.cell;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.EditEntity;
import ch.realmtech.server.registery.CellRegisterEntry;
import ch.realmtech.server.serialize.AbstractSerializerController;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import ch.realmtech.server.serialize.types.SerializedRawBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
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
            ImmutableBag<AbstractSerializerController<Integer, ? extends EditEntity>> serializerControllers = mCellPadding.get(cellToSerialize).getSerializerControllers();
            buffer.writeByte(1);
            buffer.writeByte(serializerControllers.size());
            for (int i = 0; i < serializerControllers.size(); i++) {
                SerializedApplicationBytes encode = serializerControllers.get(i).encode(cellToSerialize);
                ByteBufferHelper.writeSerializedApplicationBytes(buffer, encode);
            }
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
        EditEntity[] editEntityArgs;
        if (paddingId == 1) {
            byte cellPaddingCount = buffer.readByte();
            editEntityArgs = new EditEntity[cellPaddingCount];
            for (byte i = 0; i < cellPaddingCount; i++) {
                editEntityArgs[i] = ByteBufferHelper.decodeSerializedApplicationBytesByMagic(buffer, serializerController);
            }
        } else {
            editEntityArgs = null;
        }
        return new CellArgs(CellRegisterEntry.getCellModAndCellHash(hashCellRegisterEntry), innerChunkPos, (executeOnContext, entityId) -> {
            if (editEntityArgs != null) {
                for (int i = 0; i < editEntityArgs.length; i++) {
                    editEntityArgs[i].editEntity(executeOnContext, entityId);
                }
            }
        });
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, Integer cellToSerialize) {
        int hashCell = Integer.BYTES;
        int pos = Byte.BYTES;
        byte paddingId = Byte.BYTES;

        int paddingLength = 0;
        if (mCellPadding.has(cellToSerialize)) {
            ImmutableBag<AbstractSerializerController<Integer, ? extends EditEntity>> serializerControllers = mCellPadding.get(cellToSerialize).getSerializerControllers();
            for (int i = 0; i < serializerControllers.size(); i++) {
                paddingLength += serializerController.getApplicationBytesLength(serializerControllers.get(i), cellToSerialize);
            }
        }

        return hashCell + pos + paddingId + paddingLength;
    }

    @Override
    public byte getVersion() {
        return 4;
    }
}
