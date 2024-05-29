package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class MobAttackCoolDownPacket implements ClientPacket {
    private final UUID mobUuid;
    private final int cooldown;

    public MobAttackCoolDownPacket(UUID mobUuid, int cooldown) {
        this.mobUuid = mobUuid;
        this.cooldown = cooldown;
    }

    public MobAttackCoolDownPacket(ByteBuf byteBuf) {
        this.mobUuid = ByteBufferHelper.readUUID(byteBuf);
        this.cooldown = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.enemyAttackCoolDown(mobUuid, cooldown);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, mobUuid);
        byteBuf.writeInt(cooldown);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2 + Integer.BYTES;
    }
}
