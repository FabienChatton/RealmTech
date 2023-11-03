package ch.realmtechServer.packet.serverPacket;

import ch.realmtechServer.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class PlayerInventorySetRequestPacket implements ServerPacket {
    private final byte[] inventoryBytes;

    public PlayerInventorySetRequestPacket(byte[] inventoryBytes) {
        this.inventoryBytes = inventoryBytes;
    }

    public PlayerInventorySetRequestPacket(ByteBuf byteBuf) {
        int inventoryBytesLength = byteBuf.readInt();
        inventoryBytes = new byte[inventoryBytesLength];
        byteBuf.readBytes(inventoryBytes);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(inventoryBytes.length);
        byteBuf.writeBytes(inventoryBytes);
    }

    @Override
    public int getSize() {
        return inventoryBytes.length;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.setPlayerRequestInventory(clientChannel, inventoryBytes);
    }
}
