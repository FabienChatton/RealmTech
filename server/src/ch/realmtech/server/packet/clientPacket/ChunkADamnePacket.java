package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class ChunkADamnePacket implements ClientPacket {
    private final int chunkPosX;
    private final int chunkPosY;

    public ChunkADamnePacket(int chunkPosX, int chunkPosY) {
        this.chunkPosX = chunkPosX;
        this.chunkPosY = chunkPosY;
    }

    public ChunkADamnePacket(ByteBuf byteBuf) {
        this.chunkPosX = byteBuf.readInt();
        this.chunkPosY = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.chunkADamner(chunkPosX, chunkPosY);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(chunkPosX);
        byteBuf.writeInt(chunkPosY);
    }

    @Override
    public int getSize() {
        return Integer.SIZE * 2;
    }
}
