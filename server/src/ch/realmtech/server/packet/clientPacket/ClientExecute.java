package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.physicEntity.PhysicEntityArgs;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;

import java.util.UUID;
import java.util.function.Consumer;

public interface ClientExecute {
    ClientContext getContext();
    default SerializerController getSerializerController() {
        return getContext().getWorld().getRegistered(SerializerController.class);
    }
    void connexionJoueurReussit(ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg connexionJoueurReussitArg);

    void chunkAMounter(SerializedApplicationBytes applicationChunkBytes);

    void chunkADamner(int chunkPosX, int chunkPosY);

    void chunkARemplacer(int chunkPosX, int chunkPosY, SerializedApplicationBytes chunkApplicationBytes, int oldChunkPosX, int oldChunkPosY);

    void deconnectionJoueur(UUID uuid);

    void clientConnexionRemoved();

    void cellBreak(int worldPosX, int worldPosY);

    void cellAdd(int worldX, int worldY, SerializedApplicationBytes cellApplicationBytes);

    void tickBeat(float tickElapseTime, float deltaTime);

    <T extends ClientPacket> void packetReciveMonitoring(T packet);

    void setItemOnGroundPos(UUID uuid, ItemRegisterEntry itemRegisterEntry, float posX, float posY);

    void supprimeItemOnGround(UUID itemUuid);

    void writeOnConsoleMessage(String consoleMessageToWrite);

    void setInventory(UUID inventoryUUID, SerializedApplicationBytes applicationInventoryBytes);

    void disconnectMessage(String message);

    void timeSet(float time);
    void physicEntity(PhysicEntityArgs physicEntityArgs);

    void setPlayer(Consumer<Integer> setPlayerConsumer, UUID playerUuid);

    void playerCreateConnexion(UUID playerUuid);

    void furnaceExtraInfo(UUID furnaceUuid, int lastRemainingTickToBurnFull, int lastTickProcessFull);
}
