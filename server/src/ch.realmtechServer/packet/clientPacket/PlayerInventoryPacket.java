package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PlayerInventoryPacket implements ClientPacket {
    private final byte[] inventoryBytes;
    private final UUID playerUUID;

    public PlayerInventoryPacket(byte[] inventoryBytes, UUID playerUUID) {
        this.inventoryBytes = inventoryBytes;
        this.playerUUID = playerUUID;
    }

    public PlayerInventoryPacket(ByteBuf byteBuf) {
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        playerUUID = new UUID(msb, lsb);
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
        byteBuf.writeLong(playerUUID.getMostSignificantBits());
        byteBuf.writeLong(playerUUID.getLeastSignificantBits());
        byteBuf.writeInt(inventoryBytes.length);
        byteBuf.writeBytes(inventoryBytes);
    }

    @Override
    public int getSize() {
        return inventoryBytes.length * Byte.BYTES + Long.BYTES * 2;
    }
}
