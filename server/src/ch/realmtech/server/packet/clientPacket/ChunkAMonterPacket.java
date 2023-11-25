package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.packet.data.ChunkDataPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

public class ChunkAMonterPacket implements ClientPacket {
    private final SerializedApplicationBytes applicationChunkBytes;
    public ChunkAMonterPacket(SerializedApplicationBytes applicationChunkBytes) {
        this.applicationChunkBytes = applicationChunkBytes;
    }

    public ChunkAMonterPacket(ByteBuf byteBuf) {
        applicationChunkBytes = ByteBufferHelper.readSerializedApplicationBytes(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.chunkAMounter(applicationChunkBytes);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeSerializedApplicationBytes(byteBuf, applicationChunkBytes);
    }

    @Override
    public int getSize() {
        return applicationChunkBytes.getLength();
    }
}
