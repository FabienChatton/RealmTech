package ch.realmtechCommuns.packet.clientPacket;

import ch.realmtechCommuns.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

/**
 * Quand un joueur se connecte au serveur, les autres joueurs reçoivent se packet. Il contient
 * la position du nouveau joueur et son UUID
 */
public class ConnectionAutreJoueurPacket implements ClientPacket {
    private final float x;
    private final float y;
    private final UUID uuid;

    public ConnectionAutreJoueurPacket(float x, float y, UUID uuid) {
        this.x = x;
        this.y = y;
        this.uuid = uuid;
    }

    public ConnectionAutreJoueurPacket(ByteBuf byteBuf) {
        this.x = byteBuf.readFloat();
        this.y = byteBuf.readFloat();
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        this.uuid = new UUID(msb, lsb);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.connectionAutreJoueur(x, y, uuid);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(x);
        byteBuf.writeFloat(y);
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }
}
