package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class EnemyDeletePacket implements ClientPacket {
    private final UUID mobUuid;

    public EnemyDeletePacket(UUID mobUuid) {
        this.mobUuid = mobUuid;
    }

    public EnemyDeletePacket(ByteBuf byteBuf) {
        this.mobUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.mobDelete(mobUuid);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, mobUuid);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2;
    }
}
