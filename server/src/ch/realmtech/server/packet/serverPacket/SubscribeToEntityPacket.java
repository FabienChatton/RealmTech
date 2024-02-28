package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.UUID;

public class SubscribeToEntityPacket implements ServerPacket {
    private final UUID entityUuid;

    public SubscribeToEntityPacket(UUID entityUuid) {
        this.entityUuid = entityUuid;
    }

    public SubscribeToEntityPacket(ByteBuf byteBuf) {
        this.entityUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, entityUuid);
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.subscribeToEntity(clientChannel, entityUuid);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2;
    }
}
