package ch.realmtechServer.packet.clientPacket;

import ch.realmtechServer.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ItemOnGroundSupprimerPacket implements ClientPacket {
    private final UUID itemUuid;

    public ItemOnGroundSupprimerPacket(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public ItemOnGroundSupprimerPacket(ByteBuf byteBuf) {
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        itemUuid = new UUID(msb, lsb);
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.supprimeItemOnGround(itemUuid);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeLong(itemUuid.getMostSignificantBits());
        byteBuf.writeLong(itemUuid.getLeastSignificantBits());
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2;
    }
}
