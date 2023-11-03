package ch.realmtechServer.packet.serverPacket;

import io.netty.channel.Channel;

public interface ServerExecute {
    void newPlayerConnect(Channel clientChanel);

    void removePlayer(Channel channel);

    void playerMove(Channel clientChannel, byte inputKeys);

    void cellBreakRequest(Channel clientChannel, int chunkPosX, int chunkPosY, int itemUseByPlayerHash);

    void getPlayerInventorySession(Channel clientChannel);

    void setPlayerRequestInventory(Channel clientChannel, byte[] inventoryBytes);

    void consoleCommande(Channel clientChannel, String stringCommande);
}
