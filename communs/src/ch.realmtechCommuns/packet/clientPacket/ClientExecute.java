package ch.realmtechCommuns.packet.clientPacket;

public interface ClientExecute {
    void connectionJoueurReussit(final float x, final float y);

    void connectionAutreJoueur(float x, float y);
}
