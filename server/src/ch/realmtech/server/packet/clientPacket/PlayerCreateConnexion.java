package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PlayerCreateConnexion implements ClientPacket {
    private final UUID playerUuid;

    public PlayerCreateConnexion(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public PlayerCreateConnexion(ByteBuf byteBuf) {
        playerUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.playerCreateConnexion(playerUuid);
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
