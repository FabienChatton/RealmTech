package ch.realmtechCommuns.packet.clientPacket;

import ch.realmtechCommuns.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

public class ConnectionJoueurReussitPacket implements ClientPacket<ClientExecute> {
    private final float x;
    private final float y;
    private final int nombreAutreJoueur;
    private final float[] autreJoueurXY;

    public ConnectionJoueurReussitPacket(float x, float y, int nombreAutreJoueur, float[] autreJoueurXY) {
        this.x = x;
        this.y = y;
        this.nombreAutreJoueur = nombreAutreJoueur;
        this.autreJoueurXY = autreJoueurXY;
    }

    public ConnectionJoueurReussitPacket(ByteBuf byteBuf) {
        this.x = byteBuf.readFloat();
        this.y = byteBuf.readFloat();
        this.nombreAutreJoueur = byteBuf.readInt();
        autreJoueurXY = new float[nombreAutreJoueur * 2];
        for (int i = 0; i < autreJoueurXY.length; i++) {
            autreJoueurXY[i] = byteBuf.readFloat();
        }
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.connectionJoueurReussit(x, y);
        for (int i = 0; i < autreJoueurXY.length; i += 2) {
            clientExecute.connectionAutreJoueur(autreJoueurXY[i], autreJoueurXY[i + 1]);
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(x);
        byteBuf.writeFloat(y);
        byteBuf.writeInt(nombreAutreJoueur);
        for (int i = 0; i < autreJoueurXY.length; i++) {
            byteBuf.writeFloat(autreJoueurXY[i]);
        }
    }

}
