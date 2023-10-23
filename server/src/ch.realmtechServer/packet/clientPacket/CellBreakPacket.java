package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class CellBreakPacket implements ClientPacket {
    private final int worldPosX;
    private final int worldPosY;

    public CellBreakPacket(int worldPosX, int worldPosY) {
        this.worldPosX = worldPosX;
        this.worldPosY = worldPosY;
    }

    public CellBreakPacket(ByteBuf byteBuf) {
        worldPosX = byteBuf.readInt();
        worldPosY = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.cellBreak(worldPosX, worldPosY);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(worldPosX);
        byteBuf.writeInt(worldPosY);
    }

    @Override
    public int getSize() {
        return Integer.SIZE * 2;
    }
}
