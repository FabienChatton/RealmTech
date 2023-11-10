package ch.realmtechServer.packet.serverPacket;

import ch.realmtechServer.divers.ByteBufferHelper;
import ch.realmtechServer.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.UUID;

public class InventoryMoveItemsRequest implements ServerPacket {

    private final UUID srcInventory;
    private final UUID dstInventory;
    private final UUID[] itemsToMove;
    private final int slotIndex;

    public InventoryMoveItemsRequest(UUID srcInventory, UUID dstInventory, UUID[] itemsToMove, int slotIndex) {
        this.srcInventory = srcInventory;
        this.dstInventory = dstInventory;
        this.itemsToMove = itemsToMove;
        this.slotIndex = slotIndex;
    }

    public InventoryMoveItemsRequest(ByteBuf byteBuf) {
        srcInventory = ByteBufferHelper.readUUID(byteBuf);
        dstInventory = ByteBufferHelper.readUUID(byteBuf);
        int itemsCount = byteBuf.readInt();
        itemsToMove = new UUID[itemsCount];
        for (int i = 0; i < itemsCount; i++) {
            itemsToMove[i] = ByteBufferHelper.readUUID(byteBuf);
        }
        slotIndex = byteBuf.readInt();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, srcInventory);
        ByteBufferHelper.writeUUID(byteBuf, dstInventory);
        int itemsCount = itemsToMove.length;
        byteBuf.writeInt(itemsCount);
        for (int i = 0; i < itemsCount; i++) {
            ByteBufferHelper.writeUUID(byteBuf, itemsToMove[i]);
        }
        byteBuf.writeInt(slotIndex);
    }

    @Override
    public int getSize() {
        // uuid src and dst, itemsToMove length * item uid, slot index
        return 2 * (Long.BYTES * 2) + itemsToMove.length + itemsToMove.length * (Long.BYTES * 2) + Integer.BYTES;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.inventoryMoveItems(clientChannel, srcInventory, dstInventory, itemsToMove, slotIndex);
    }
}
