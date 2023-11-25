package ch.realmtech.server.serialize.chunk;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.ecs.component.InfCellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.serialize.Serializer;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.cell.CellArgs;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
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

        buffer.writeInt(chunkToSerialize.chunkPosX);
        buffer.writeInt(chunkToSerialize.chunkPosY);
        buffer.writeShort(chunkToSerialize.infCellsId.length);

        for (int i = 0; i < chunkToSerialize.infCellsId.length; i++) {
            InfCellComponent infCellComponent = mCell.get(chunkToSerialize.infCellsId[i]);

            ByteBufferHelper.encodeSerializedApplicationBytes(buffer, serializerController.getCellSerializerController(), infCellComponent);
        }
        return new SerializedRawBytes(buffer.array());
    }

    @Override
    public Integer fromBytes(World world, SerializerController serializerController, SerializedRawBytes rawBytes) {
        ByteBuf buffer = Unpooled.wrappedBuffer(rawBytes.rawBytes());

        int chunkId = world.create();

        int chunkPosX = buffer.readInt();
        int chunkPosY = buffer.readInt();
        short cellCount = buffer.readShort();

        int[] cells = new int[cellCount];
        world.edit(chunkId).create(InfChunkComponent.class).set(chunkPosX, chunkPosY, cells);

        for (int i = 0; i < cells.length; i++) {
            CellArgs cellArgs = ByteBufferHelper.decodeSerializedApplicationBytes(buffer, serializerController.getCellSerializerController());

            int cellId = world.getSystem(MapManager.class).newCell(chunkId, chunkPosX, chunkPosY, Cells.getInnerChunkPosX(cellArgs.innerChunk()), Cells.getInnerChunkPosY(cellArgs.innerChunk()), cellArgs.cellRegisterEntry());
            cells[i] = cellId;
        }
        return chunkId;
    }

    @Override
    public int getBytesSize(World world, SerializerController serializerController, InfChunkComponent chunkToSerialize) {
        ComponentMapper<InfCellComponent> mCell = world.getMapper(InfCellComponent.class);
        int pos = Integer.BYTES * 2;
        int cellCount = Short.BYTES;

        int cellByteLength = Integer.BYTES * chunkToSerialize.infCellsId.length;
        int cellTotalSize = 0;
        for (int i = 0; i < chunkToSerialize.infCellsId.length; i++) {
            InfCellComponent infCellComponent = mCell.get(chunkToSerialize.infCellsId[i]);
            cellTotalSize += serializerController.getApplicationBytesLength(serializerController.getCellSerializerController(), infCellComponent);
        }
        return pos + cellCount + cellByteLength + cellTotalSize;
    }

    @Override
    public byte getVersion() {
        return 9;
    }
}
