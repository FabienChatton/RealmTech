package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PlayerPickUpItem implements ClientPacket {
    private final UUID playerUuid;

    public PlayerPickUpItem(ByteBuf byteBuf) {
        playerUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    public PlayerPickUpItem(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.playerPickUpItem(playerUuid);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, playerUuid);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2;
    }
}
