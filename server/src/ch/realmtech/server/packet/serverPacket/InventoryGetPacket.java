package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.UUID;

public class InventoryGetPacket implements ServerPacket {
    private final UUID inventoryUuid;

    public InventoryGetPacket(UUID inventoryUuid) {
        this.inventoryUuid = inventoryUuid;
    }

    public InventoryGetPacket(ByteBuf byteBuf) {
        this.inventoryUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, inventoryUuid);
    }

    @Override
    public int getSize() {
        return Integer.BYTES * 2;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.getInventory(clientChannel, inventoryUuid);
    }
}
