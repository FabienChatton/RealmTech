package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.packet.ClientPacket;

import java.util.UUID;

public interface ClientExecute {
    void connexionJoueurReussit(final float x, final float y, UUID uuid);

    void autreJoueur(float x, float y, UUID uuid);

    void chunkAMounter(int chunkPosX, int chunkPosY, byte[] chunkBytes);

    void chunkADamner(int chunkPosX, int chunkPosY);

    void chunkARemplacer(int chunkPosX, int chunkPosY, byte[] chunkBytes, int oldChunkPosX, int oldChunkPosY);

    void deconnectionJoueur(UUID uuid);

    void clientConnexionRemoved();

    void cellBreak(int chunkPosX, int chunkPosY, byte innerChunkX, byte innerChunkY, UUID playerUUID, int itemUsedByPlayerHash);

    void tickBeat(float tickElapseTime);

    <T extends ClientPacket> void packetReciveMonitoring(T packet);
}
