package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.UUID;

public class UnSubscribeToEntityPacket implements ServerPacket {
    private final UUID entityUuid;

    public UnSubscribeToEntityPacket(UUID entityUuid) {
        this.entityUuid = entityUuid;
    }

    public UnSubscribeToEntityPacket(ByteBuf byteBuf) {
        this.entityUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, entityUuid);
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.unSubscribeToEntity(clientChannel, entityUuid);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2;
    }
}
