package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.divers.ByteBufferHelper;
import ch.realmtechServer.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PlayerInventoryPacket implements ClientPacket {
    private final UUID playerUUID;
    private final byte[] inventoryBytes;
    private final UUID inventoryUUID;

    public PlayerInventoryPacket(UUID playerUUID, byte[] inventoryBytes, UUID inventoryUUID) {
        this.playerUUID = playerUUID;
        this.inventoryBytes = inventoryBytes;
        this.inventoryUUID = inventoryUUID;
    }

    public PlayerInventoryPacket(ByteBuf byteBuf) {
        playerUUID = ByteBufferHelper.readUUID(byteBuf);
        int inventoryBytesLength = byteBuf.readInt();
        inventoryBytes = new byte[inventoryBytesLength];
        byteBuf.readBytes(inventoryBytes);
        inventoryUUID = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.setPlayerInventory(playerUUID, inventoryBytes, inventoryUUID);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, playerUUID);
        byteBuf.writeInt(inventoryBytes.length);
        byteBuf.writeBytes(inventoryBytes);
        ByteBufferHelper.writeUUID(byteBuf, inventoryUUID);
    }

    @Override
    public int getSize() {
        return inventoryBytes.length * Byte.BYTES + Long.BYTES * 2;
    }
}
