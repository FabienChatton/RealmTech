package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * Le serveur donne se packet quand le joueur initie la connexion et que la connexion, et réussie.
 * Reçoit les informations sur sa position dans le monde et sont UUID.
 */
public class ConnexionJoueurReussitPacket implements ClientPacket {
    private final float x;
    private final float y;
    private final UUID uuid;

    public ConnexionJoueurReussitPacket(float x, float y, UUID uuid) {
        this.x = x;
        this.y = y;
        this.uuid = uuid;
    }

    public ConnexionJoueurReussitPacket(ByteBuf byteBuf) {
        this.x = byteBuf.readFloat();
        this.y = byteBuf.readFloat();
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        this.uuid = new UUID(msb, lsb);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.connexionJoueurReussit(x, y, uuid);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(x);
        byteBuf.writeFloat(y);
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    public record ConnexionJoueurReussitArg(float x, float y, UUID uuid) {

    }
    @Override
    public int getSize() {
        return Float.SIZE * 2 + Long.SIZE * 2;
    }
}
