package ch.realmtechServer.packet.serverPacket;

import ch.realmtechServer.level.cell.Cells;
import ch.realmtechServer.packet.ServerPacket;
import ch.realmtechServer.registery.ItemRegisterEntry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class CellBreakRequestPacket implements ServerPacket {
    private final int chunkPosX;
    private final int chunkPosY;
    private final byte innerChunkX;
    private final byte innerChunkY;
    private final int itemUseByPlayerHash;

    public CellBreakRequestPacket(int chunkPosX, int chunkPosY, byte innerChunkX, byte innerChunkY, ItemRegisterEntry itemUseByPlayerHash) {
        this.chunkPosX = chunkPosX;
        this.chunkPosY = chunkPosY;
        this.innerChunkX = innerChunkX;
        this.innerChunkY = innerChunkY;
        this.itemUseByPlayerHash = itemUseByPlayerHash.getHash();
    }

    public CellBreakRequestPacket(ByteBuf byteBuf) {
        chunkPosX = byteBuf.readInt();
        chunkPosY = byteBuf.readInt();
        byte innerChunk = byteBuf.readByte();
        innerChunkX = Cells.getInnerChunkPosX(innerChunk);
        innerChunkY = Cells.getInnerChunkPosY(innerChunk);
        itemUseByPlayerHash = byteBuf.readInt();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(chunkPosX);
        byteBuf.writeInt(chunkPosY);
        byteBuf.writeByte(Cells.getInnerChunkPos(innerChunkX, innerChunkY));
        byteBuf.writeInt(itemUseByPlayerHash);
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.cellBreakRequest(clientChannel, chunkPosX, chunkPosY, innerChunkX, innerChunkY, itemUseByPlayerHash);
    }
}
