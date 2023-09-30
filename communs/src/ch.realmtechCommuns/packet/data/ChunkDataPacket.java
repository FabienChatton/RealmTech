package ch.realmtechCommuns.packet.data;

import io.netty.buffer.ByteBuf;

public class ChunkDataPacket {
    private final int chunkPosX;
    private final int chunkPosY;
    private final byte[] chunkBytes;

    public ChunkDataPacket(int chunkPosX, int chunkPosY, byte[] chunkBytes) {
        this.chunkPosX = chunkPosX;
        this.chunkPosY = chunkPosY;
        this.chunkBytes = chunkBytes;
    }

    public ChunkDataPacket(ByteBuf byteBuf) {
        chunkPosX = byteBuf.readInt();
        chunkPosY = byteBuf.readInt();
        int chunkBytesLength = byteBuf.readInt();
        chunkBytes = new byte[chunkBytesLength];
        byteBuf.readBytes(chunkBytes);
    }

    public void writeChunk(ByteBuf byteBuf) {
        byteBuf.writeInt(chunkPosX);
        byteBuf.writeInt(chunkPosY);
        byteBuf.writeInt(chunkBytes.length);
        byteBuf.writeBytes(chunkBytes);
    }

    public int getChunkPosX() {
        return chunkPosX;
    }

    public int getChunkPosY() {
        return chunkPosY;
    }

    public byte[] getChunkBytes() {
        return chunkBytes;
    }
}
