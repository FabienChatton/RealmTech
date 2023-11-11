package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * Donne au client toutes les informations sur les joueurs qui sont présents sur le serveur.
 */
public class TousLesJoueurPacket implements ClientPacket {
    private final int nombreDeJoueur;
    private final Vector2[] poss;
    private final UUID[] uuids;

    public TousLesJoueurPacket(int nombreDeJoueur, Vector2[] poss, UUID[] uuids) {
        this.nombreDeJoueur = nombreDeJoueur;
        this.poss = poss;
        this.uuids = uuids;
    }

    public TousLesJoueurPacket(ByteBuf byteBuf) {
        nombreDeJoueur = byteBuf.readInt();
        poss = new Vector2[nombreDeJoueur];
        uuids = new UUID[nombreDeJoueur];
        for (int i = 0, j = 0; i < nombreDeJoueur; i++, j += 2) {
            float x = byteBuf.readFloat();
            float y = byteBuf.readFloat();
            poss[i] = new Vector2(x, y);
            long msb = byteBuf.readLong();
            long lsb = byteBuf.readLong();
            uuids[i] = new UUID(msb, lsb);
        }
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        for (int i = 0; i < nombreDeJoueur; i++) {
            clientExecute.autreJoueur(poss[i].x, poss[i].y, uuids[i]);
        }
    }

    @Override
    public int getSize() {
        return Integer.SIZE + poss.length * Float.SIZE + uuids.length * Long.SIZE;
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(nombreDeJoueur);
        for (int i = 0; i < nombreDeJoueur; i++) {
            byteBuf.writeFloat(poss[i].x);
            byteBuf.writeFloat(poss[i].y);
            byteBuf.writeLong(uuids[i].getMostSignificantBits());
            byteBuf.writeLong(uuids[i].getLeastSignificantBits());
        }
    }
}
