package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.UUID;

public class EatItemPacket implements ServerPacket {
    private final UUID itemUuid;

    public EatItemPacket(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public EatItemPacket(ByteBuf byteBuf) {
        this.itemUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.eatItem(clientChannel, itemUuid);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, itemUuid);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2;
    }
}
