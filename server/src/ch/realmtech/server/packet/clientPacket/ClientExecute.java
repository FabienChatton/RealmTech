package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.physicEntity.PhysicEntityArgs;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.badlogic.gdx.math.Vector2;

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

    void cellSet(int worldPosX, int worldPosY, byte layer, SerializedApplicationBytes cellApplicationBytes);

    void tickBeat(float tickElapseTime, float deltaTime);

    void setItemOnGroundPos(UUID uuid, int itemRegisterEntryHash, float posX, float posY);

    void supprimeItemOnGround(UUID itemUuid);

    void writeOnConsoleMessage(String consoleMessageToWrite);

    void setInventory(UUID inventoryUUID, SerializedApplicationBytes applicationInventoryBytes);

    void disconnectMessage(String message);
    void timeSet(float time);

    void physicEntity(PhysicEntityArgs physicEntityArgs);

    void setPlayer(Consumer<Integer> setPlayerConsumer, UUID playerUuid);

    void playerOutOfRange(UUID playerUuid);

    void playerCreateConnexion(UUID playerUuid);

    void furnaceExtraInfo(UUID furnaceUuid, int lastRemainingTickToBurnFull, int lastTickProcessFull);

    void energyBatterySetEnergy(UUID energyBatteryUuid, long stored);

    void energyGeneratorSetInfo(UUID energyGeneratorUuid, int remainingTickToBurn, int lastRemainingTickToBurn);

    void playerPickUpItem(UUID playerUuid);

    void mobDelete(UUID mobUuid);

    void addParticle(ParticleAddPacket.Particles particle, Vector2 worldPos);

    void mobAttackCoolDown(UUID modUuid, int cooldown);

    void nextFrame(Runnable runnable);

    void questSetCompleted(int questEntryId, long completedTimestamp);

    void playerCreateQuest(SerializedApplicationBytes questSerializedApplicationBytes);
}
