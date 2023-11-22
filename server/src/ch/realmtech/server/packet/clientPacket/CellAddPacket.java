package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class CellAddPacket implements ClientPacket {
    private final int worldPosX;
    private final int worldPosY;
    private final int cellHash;

    public CellAddPacket(int worldPosX, int worldPosY, int cellHash) {
        this.worldPosX = worldPosX;
        this.worldPosY = worldPosY;
        this.cellHash = cellHash;
    }

    public CellAddPacket(ByteBuf byteBuf) {
        worldPosX = byteBuf.readInt();
        worldPosY = byteBuf.readInt();
        cellHash = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.cellAdd(worldPosX, worldPosY, cellHash);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(worldPosX);
        byteBuf.writeInt(worldPosY);
        byteBuf.writeInt(cellHash);
    }

    @Override
    public int getSize() {
        return Integer.BYTES * 3;
    }
}
