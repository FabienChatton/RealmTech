package ch.realmtechCommuns.packet.clientPacket;

import java.util.UUID;

public interface ClientExecute {
    void connectionJoueurReussit(final float x, final float y, UUID uuid);

    void connectionAutreJoueur(float x, float y, UUID uuid);
}
