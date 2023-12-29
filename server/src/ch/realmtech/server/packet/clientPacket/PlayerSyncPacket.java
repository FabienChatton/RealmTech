package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

import java.util.UUID;
import java.util.function.Consumer;

public class PlayerSyncPacket implements ClientPacket {
    private final SerializedApplicationBytes playerBytes;
    private final UUID playerUuid;

    public PlayerSyncPacket(SerializedApplicationBytes playerBytes, UUID playerUuid) {
        this.playerBytes = playerBytes;
        this.playerUuid = playerUuid;
    }

    public PlayerSyncPacket(ByteBuf byteBuf) {
        playerBytes = ByteBufferHelper.readSerializedApplicationBytes(byteBuf);
        playerUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        Consumer<Integer> setPlayerConsumer = clientExecute.getSerializerController().getPlayerSerializerController().decode(playerBytes);
        clientExecute.setPlayer(setPlayerConsumer, playerUuid);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeSerializedApplicationBytes(byteBuf, playerBytes);
        ByteBufferHelper.writeUUID(byteBuf, playerUuid);
    }

    @Override
    public int getSize() {
        int playerBytesLength = playerBytes.getLength();
        int playerUuidLength = Long.BYTES * 2;
        return playerBytesLength + playerUuidLength;
    }
}
