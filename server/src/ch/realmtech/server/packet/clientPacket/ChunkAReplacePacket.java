package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.packet.data.ChunkDataPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

public class ChunkAReplacePacket implements ClientPacket {
    private final int newChunkPosX;
    private final int newChunkPosY;
    private final SerializedApplicationBytes applicationChunkBytes;
    private final int oldChunkPosX;
    private final int oldChunkPosY;

    public ChunkAReplacePacket(int newChunkPosX, int newChunkPosY, SerializedApplicationBytes applicationChunkBytes, int oldChunkPosX, int oldChunkPosY) {
        this.newChunkPosX = newChunkPosX;
        this.newChunkPosY = newChunkPosY;
        this.applicationChunkBytes = applicationChunkBytes;
        this.oldChunkPosX = oldChunkPosX;
        this.oldChunkPosY = oldChunkPosY;
    }

    public ChunkAReplacePacket(ByteBuf byteBuf) {
        newChunkPosX = byteBuf.readInt();
        newChunkPosY = byteBuf.readInt();
        int applicationChunkBytesLength = byteBuf.readInt();
        byte[] chunkBytes = new byte[applicationChunkBytesLength];
        byteBuf.readBytes(chunkBytes);
        applicationChunkBytes = new SerializedApplicationBytes(chunkBytes);
        oldChunkPosX = byteBuf.readInt();
        oldChunkPosY = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.chunkARemplacer(newChunkPosX, newChunkPosY, applicationChunkBytes, oldChunkPosX, oldChunkPosY);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(newChunkPosX);
        byteBuf.writeInt(newChunkPosY);
        byteBuf.writeInt(applicationChunkBytes.getLength());
        byteBuf.writeBytes(applicationChunkBytes.applicationBytes());
        byteBuf.writeInt(oldChunkPosX);
        byteBuf.writeInt(oldChunkPosY);
    }

    @Override
    public int getSize() {
        return applicationChunkBytes.getLength() + Integer.BYTES + Integer.SIZE * 2;
    }
}
