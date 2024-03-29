package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class DeconnectionJoueurPacket implements ClientPacket {
    private final UUID uuid;

    public DeconnectionJoueurPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public DeconnectionJoueurPacket(ByteBuf byteBuf) {
        long msb = byteBuf.readLong();
        long lsm = byteBuf.readLong();
        uuid = new UUID(msb, lsm);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.deconnectionJoueur(uuid);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    @Override
    public int getSize() {
        return Long.SIZE * 2;
    }
}
