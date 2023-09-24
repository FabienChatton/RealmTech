package ch.realmtechCommuns.packet.clientPacket;

import ch.realmtechCommuns.packet.ClientPacket;
import com.badlogic.gdx.Gdx;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ChunkAMonterPacket implements ClientPacket {
    private final int chunkPosX;
    private final int chunkPosY;
    private final UUID uuid;
    private final byte[] chunkBytes;
    public ChunkAMonterPacket(int chunkPosX, int chunkPosY, UUID uuid, byte[] chunkBytes) {
        this.chunkPosX = chunkPosX;
        this.chunkPosY = chunkPosY;
        this.uuid = uuid;
        this.chunkBytes = chunkBytes;
    }

    public ChunkAMonterPacket(ByteBuf byteBuf) {
        chunkPosX = byteBuf.readInt();
        chunkPosY = byteBuf.readInt();
        long uuidMsb = byteBuf.readLong();
        long uuidLsb = byteBuf.readLong();
        uuid = new UUID(uuidMsb, uuidLsb);
        int chunkBytesLength = byteBuf.readInt();
        chunkBytes = new byte[chunkBytesLength];
        byteBuf.readBytes(chunkBytes);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        Gdx.app.postRunnable(() -> clientExecute.chunkAMounter(chunkPosX, chunkPosY, uuid, chunkBytes));
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(chunkPosX);
        byteBuf.writeInt(chunkPosY);
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
        byteBuf.writeInt(chunkBytes.length);
        byteBuf.writeBytes(chunkBytes);
    }
}
