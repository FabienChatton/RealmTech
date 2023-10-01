package ch.realmtechCommuns.packet.clientPacket;

import ch.realmtechCommuns.packet.ClientPacket;
import ch.realmtechCommuns.packet.data.ChunkDataPacket;
import io.netty.buffer.ByteBuf;

public class ChunkAMonterPacket implements ClientPacket {
    private final ChunkDataPacket chunkDataPacket;
    public ChunkAMonterPacket(int chunkPosX, int chunkPosY, byte[] chunkBytes) {
        chunkDataPacket = new ChunkDataPacket(chunkPosX, chunkPosY, chunkBytes);
    }

    public ChunkAMonterPacket(ByteBuf byteBuf) {
        chunkDataPacket = new ChunkDataPacket(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.chunkAMounter(chunkDataPacket.getChunkPosX(), chunkDataPacket.getChunkPosY(), chunkDataPacket.getChunkBytes());
    }

    @Override
    public void write(ByteBuf byteBuf) {
        chunkDataPacket.writeChunk(byteBuf);
    }
}
