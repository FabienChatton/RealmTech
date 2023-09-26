package ch.realmtechCommuns.packet.clientPacket;

import ch.realmtechCommuns.packet.ClientPacket;
import com.badlogic.gdx.Gdx;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ChunkAMonterPacket implements ClientPacket {
    private final int chunkPosX;
    private final int chunkPosY;
    private final byte[] chunkBytes;
    public ChunkAMonterPacket(int chunkPosX, int chunkPosY, byte[] chunkBytes) {
        this.chunkPosX = chunkPosX;
        this.chunkPosY = chunkPosY;
        this.chunkBytes = chunkBytes;
    }

    public ChunkAMonterPacket(ByteBuf byteBuf) {
        chunkPosX = byteBuf.readInt();
        chunkPosY = byteBuf.readInt();
        int chunkBytesLength = byteBuf.readInt();
        chunkBytes = new byte[chunkBytesLength];
        byteBuf.readBytes(chunkBytes);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        Gdx.app.postRunnable(() -> clientExecute.chunkAMounter(chunkPosX, chunkPosY, chunkBytes));
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(chunkPosX);
        byteBuf.writeInt(chunkPosY);
        byteBuf.writeInt(chunkBytes.length);
        byteBuf.writeBytes(chunkBytes);
    }
}
