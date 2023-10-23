package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.packet.ClientPacket;
import ch.realmtechServer.registery.ItemRegisterEntry;

import java.util.UUID;

public interface ClientExecute {
    void connexionJoueurReussit(final float x, final float y, UUID uuid);

    void autreJoueur(float x, float y, UUID uuid);

    void chunkAMounter(int chunkPosX, int chunkPosY, byte[] chunkBytes);

    void chunkADamner(int chunkPosX, int chunkPosY);

    void chunkARemplacer(int chunkPosX, int chunkPosY, byte[] chunkBytes, int oldChunkPosX, int oldChunkPosY);

    void deconnectionJoueur(UUID uuid);

    void clientConnexionRemoved();

    void cellBreak(int worldPosX, int worldPosY);

    void tickBeat(float tickElapseTime);

    <T extends ClientPacket> void packetReciveMonitoring(T packet);

    void setPlayerInventory(UUID playerUUID, byte[] inventoryBytes);

    void setItemOnGroundPos(UUID uuid, ItemRegisterEntry itemRegisterEntry, float posX, float posY);
}
