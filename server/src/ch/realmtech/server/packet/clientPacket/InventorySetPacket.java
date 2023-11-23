package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class InventorySetPacket implements ClientPacket {

    private final UUID inventoryUUID;
    private final SerializedApplicationBytes applicationInventoryBytes;

    public InventorySetPacket(UUID inventoryUUID, SerializedApplicationBytes applicationInventoryBytes) {
        this.inventoryUUID = inventoryUUID;
        this.applicationInventoryBytes = applicationInventoryBytes;
    }

    public InventorySetPacket(ByteBuf byteBuf) {
        inventoryUUID = ByteBufferHelper.readUUID(byteBuf);
        int inventoryBytesLength = byteBuf.readInt();
        applicationInventoryBytes = new SerializedApplicationBytes(new byte[inventoryBytesLength]);
        byteBuf.readBytes(applicationInventoryBytes.applicationBytes());
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, inventoryUUID);
        byteBuf.writeInt(applicationInventoryBytes.applicationBytes().length);
        byteBuf.writeBytes(applicationInventoryBytes.applicationBytes());
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.setInventory(inventoryUUID, applicationInventoryBytes);
    }

    @Override
    public int getSize() {
        return 2 * Long.BYTES + Integer.BYTES + applicationInventoryBytes.applicationBytes().length * Byte.BYTES;
    }
}
