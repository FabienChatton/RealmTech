package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.packet.ClientPacket;
import ch.realmtechServer.packet.data.ChunkDataPacket;
import io.netty.buffer.ByteBuf;

public class ChunkAReplacePacket implements ClientPacket {
    private final ChunkDataPacket newChunkDataPacket;
    private final int oldChunkPosX;
    private final int oldChunkPosY;

    public ChunkAReplacePacket(int newChunkPosX, int newChunkPosY, byte[] chunkBytes, int oldChunkPosX, int oldChunkPosY) {
        newChunkDataPacket = new ChunkDataPacket(newChunkPosX, newChunkPosY, chunkBytes);
        this.oldChunkPosX = oldChunkPosX;
        this.oldChunkPosY = oldChunkPosY;
    }

    public ChunkAReplacePacket(ByteBuf byteBuf) {
        newChunkDataPacket = new ChunkDataPacket(byteBuf);
        oldChunkPosX = byteBuf.readInt();
        oldChunkPosY = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.chunkARemplacer(newChunkDataPacket.getChunkPosX(), newChunkDataPacket.getChunkPosY(), newChunkDataPacket.getChunkBytes(), oldChunkPosX, oldChunkPosY);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        newChunkDataPacket.writeChunk(byteBuf);
        byteBuf.writeInt(oldChunkPosX);
        byteBuf.writeInt(oldChunkPosY);
    }

    @Override
    public int getSize() {
        return newChunkDataPacket.getSize() + Integer.SIZE * 2;
    }
}
