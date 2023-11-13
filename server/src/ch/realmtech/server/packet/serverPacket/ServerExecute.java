package ch.realmtech.server.packet.serverPacket;

import io.netty.channel.Channel;

import java.util.UUID;

public interface ServerExecute {
    void newPlayerConnect(Channel clientChanel);

    void removePlayer(Channel channel);

    void playerMove(Channel clientChannel, byte inputKeys);

    void cellBreakRequest(Channel clientChannel, int chunkPosX, int chunkPosY, int itemUseByPlayerHash);

    void getPlayerInventorySession(Channel clientChannel);

    void consoleCommande(Channel clientChannel, String stringCommande);

    void inventoryMoveItems(Channel clientChannel, UUID srcInventory, UUID dstInventory, UUID[] itemsToMove, int slotIndex);

    void getInventory(Channel clientChannel, UUID inventoryUuid);
}
