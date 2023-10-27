package ch.realmtechServer.packet.serverPacket;

import ch.realmtechServer.packet.ServerPacket;
import ch.realmtechServer.registery.ItemRegisterEntry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class CellBreakRequestPacket implements ServerPacket {
    private final int worldPosX;
    private final int worldPosY;
    private final int itemUseByPlayerHash;

    public CellBreakRequestPacket(int worldPosX, int worldPosY, ItemRegisterEntry itemUseByPlayerHash) {
        this.worldPosX = worldPosX;
        this.worldPosY = worldPosY;
        this.itemUseByPlayerHash = itemUseByPlayerHash.getHash();
    }

    public CellBreakRequestPacket(ByteBuf byteBuf) {
        worldPosX = byteBuf.readInt();
        worldPosY = byteBuf.readInt();
        itemUseByPlayerHash = byteBuf.readInt();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(worldPosX);
        byteBuf.writeInt(worldPosY);
        byteBuf.writeInt(itemUseByPlayerHash);
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.cellBreakRequest(clientChannel, worldPosX, worldPosY, itemUseByPlayerHash);
    }

    @Override
    public int getSize() {
        return Integer.SIZE * 3;
    }
}
