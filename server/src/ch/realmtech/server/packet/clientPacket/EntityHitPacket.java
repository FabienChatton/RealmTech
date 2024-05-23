package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class EntityHitPacket implements ClientPacket {
    private final UUID enemyUuid;

    public EntityHitPacket(UUID enemyUuid) {
        this.enemyUuid = enemyUuid;
    }

    public EntityHitPacket(ByteBuf byteBuf) {
        this.enemyUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.nextFrame(() -> clientExecute.getContext().getSystemsAdminClient().getHitManagerForClient().entityJustHit(enemyUuid));
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, enemyUuid);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2;
    }
}
