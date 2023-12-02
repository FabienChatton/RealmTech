package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

public class CellAddPacket implements ClientPacket {
    private final int worldX;
    private final int worldY;
    private final SerializedApplicationBytes cellApplicationBytes;

    public CellAddPacket(int worldX, int worldY, SerializedApplicationBytes cellApplicationBytes) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.cellApplicationBytes = cellApplicationBytes;
    }

    public CellAddPacket(ByteBuf byteBuf) {
        cellApplicationBytes = ByteBufferHelper.readSerializedApplicationBytes(byteBuf);
        worldX = byteBuf.readInt();
        worldY = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.cellAdd(worldX, worldY, cellApplicationBytes);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeSerializedApplicationBytes(byteBuf, cellApplicationBytes);
        byteBuf.writeInt(worldX);
        byteBuf.writeInt(worldY);
    }

    @Override
    public int getSize() {
        return cellApplicationBytes.getLength() + 2 * Integer.BYTES;
    }
}
