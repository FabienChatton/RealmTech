package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class InventorySetPacket implements ClientPacket {

    private final UUID inventoryUUID;
    private final byte[] inventoryBytes;

    public InventorySetPacket(UUID inventoryUUID, byte[] inventoryBytes) {
        this.inventoryUUID = inventoryUUID;
        this.inventoryBytes = inventoryBytes;
    }

    public InventorySetPacket(ByteBuf byteBuf) {
        inventoryUUID = ByteBufferHelper.readUUID(byteBuf);
        int inventoryBytesLength = byteBuf.readInt();
        inventoryBytes = new byte[inventoryBytesLength];
        byteBuf.readBytes(inventoryBytes);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, inventoryUUID);
        byteBuf.writeInt(inventoryBytes.length);
        byteBuf.writeBytes(inventoryBytes);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.setInventory(inventoryUUID, inventoryBytes);
    }

    @Override
    public int getSize() {
        return 2 * Long.BYTES + Integer.BYTES + inventoryBytes.length * Byte.BYTES;
    }
}
