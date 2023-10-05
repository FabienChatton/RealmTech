package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.level.cell.Cells;
import ch.realmtechServer.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class CellBreakPacket implements ClientPacket {
    private final int chunkPosX;
    private final int chunkPosY;
    private final byte innerChunkX;
    private final byte innerChunkY;
    private final UUID playerUUID;
    private final int itemUsedByPlayerHash;

    public CellBreakPacket(int chunkPosX, int chunkPosY, byte innerChunkX, byte innerChunkY, UUID playerUUID, int itemUsedByPlayerHash) {
        this.chunkPosX = chunkPosX;
        this.chunkPosY = chunkPosY;
        this.innerChunkX = innerChunkX;
        this.innerChunkY = innerChunkY;
        this.playerUUID = playerUUID;
        this.itemUsedByPlayerHash = itemUsedByPlayerHash;
    }

    public CellBreakPacket(ByteBuf byteBuf) {
        chunkPosX = byteBuf.readInt();
        chunkPosY = byteBuf.readInt();
        byte innerChunk = byteBuf.readByte();
        innerChunkX = Cells.getInnerChunkPosX(innerChunk);
        innerChunkY = Cells.getInnerChunkPosY(innerChunk);
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        playerUUID = new UUID(msb, lsb);
        itemUsedByPlayerHash = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.cellBreak(chunkPosX, chunkPosY, innerChunkX, innerChunkY, playerUUID, itemUsedByPlayerHash);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(chunkPosX);
        byteBuf.writeInt(chunkPosY);
        byteBuf.writeByte(Cells.getInnerChunkPos(innerChunkX, innerChunkY));
        byteBuf.writeLong(playerUUID.getMostSignificantBits());
        byteBuf.writeLong(playerUUID.getLeastSignificantBits());
        byteBuf.writeInt(itemUsedByPlayerHash);
    }
}
