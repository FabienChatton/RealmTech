package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

public class CellSetPacket implements ClientPacket {
    private final int worldPosX;
    private final int worldPosY;
    private final byte layer;
    private final SerializedApplicationBytes cellApplicationBytes;

    public CellSetPacket(int worldPosX, int worldPosY, byte layer, SerializedApplicationBytes cellApplicationBytes) {
        this.worldPosX = worldPosX;
        this.worldPosY = worldPosY;
        this.layer = layer;
        this.cellApplicationBytes = cellApplicationBytes;
    }

    public CellSetPacket(ByteBuf byteBuf) {
        worldPosX = byteBuf.readInt();
        worldPosY = byteBuf.readInt();
        layer = byteBuf.readByte();
        cellApplicationBytes = ByteBufferHelper.readSerializedApplicationBytes(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.cellSet(worldPosX, worldPosY, layer, cellApplicationBytes);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(worldPosX);
        byteBuf.writeInt(worldPosY);
        byteBuf.writeByte(layer);
        ByteBufferHelper.writeSerializedApplicationBytes(byteBuf, cellApplicationBytes);
    }

    @Override
    public int getSize() {
        return Integer.BYTES * 2 + Byte.BYTES + cellApplicationBytes.getLength();
    }
}
