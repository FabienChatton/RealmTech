package ch.realmtech.server.packet.serverPacket;

import com.badlogic.gdx.utils.Null;
import io.netty.channel.Channel;

import java.util.UUID;

public interface ServerExecute {
    void connexionPlayerRequest(Channel clientChanel, String username);

    void removePlayer(Channel channel);

    void playerMove(Channel clientChannel, byte inputKeys);

    void cellBreakRequest(Channel clientChannel, int chunkPosX, int chunkPosY, @Null UUID itemUsedUuid);

    void getPlayerInventorySession(Channel clientChannel);

    void consoleCommande(Channel clientChannel, String stringCommande);

    void moveStackToStackNumberRequest(Channel clientChannel, UUID srcInventory, UUID dstInventory, UUID[] itemsToMove, int slotIndex);

    void getInventory(Channel clientChannel, UUID inventoryUuid);

    void itemToCellPlace(Channel clientChannel, UUID itemToPlaceUuid, int worldX, int worldY);

    void getTime(Channel clientChannel);

    void rotateFaceCellRequest(Channel clientChannel, int worldPosX, int worldPosY, byte layer, byte faceToRotate);

    void subscribeToEntity(Channel clientChannel, UUID entityUuid);

    void unSubscribeToEntity(Channel clientChannel, UUID entityUuid);
}
