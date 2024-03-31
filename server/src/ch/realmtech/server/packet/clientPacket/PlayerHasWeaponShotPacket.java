package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PlayerHasWeaponShotPacket implements ClientPacket {
    private final UUID playerUuid;

    public PlayerHasWeaponShotPacket(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public PlayerHasWeaponShotPacket(ByteBuf byteBuf) {
        playerUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.getContext().getSystemsAdminClient().getWeaponManagerClient().playerHasJustShoot(playerUuid);
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
