package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PlayerInventoryPacket implements ClientPacket {
    private final UUID playerUUID;
    private final byte[] inventoryBytes;

    public PlayerInventoryPacket(UUID playerUUID, byte[] inventoryBytes) {
        this.playerUUID = playerUUID;
        this.inventoryBytes = inventoryBytes;
    }

    public PlayerInventoryPacket(ByteBuf byteBuf) {
        playerUUID = ByteBufferHelper.readUUID(byteBuf);
        int inventoryBytesLength = byteBuf.readInt();
        inventoryBytes = new byte[inventoryBytesLength];
        byteBuf.readBytes(inventoryBytes);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.setPlayerInventory(playerUUID, inventoryBytes);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, playerUUID);
        byteBuf.writeInt(inventoryBytes.length);
        byteBuf.writeBytes(inventoryBytes);
    }

    @Override
    public int getSize() {
        return inventoryBytes.length * Byte.BYTES + Long.BYTES * 2;
    }
}
