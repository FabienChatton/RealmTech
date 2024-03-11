package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ItemOnGroundPacket implements ClientPacket {

    private final UUID uuid;
    private final float worldPosX;
    private final float worldPosY;
    private final int itemRegisterEntryId;

    public ItemOnGroundPacket(UUID uuid, int itemRegisterEntryId, float worldPosX, float worldPosY) {
        this.uuid = uuid;
        this.worldPosX = worldPosX;
        this.worldPosY = worldPosY;
        this.itemRegisterEntryId = itemRegisterEntryId;
    }

    public ItemOnGroundPacket(ByteBuf byteBuf) {
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        uuid = new UUID(msb, lsb);
        worldPosX = byteBuf.readFloat();
        worldPosY = byteBuf.readFloat();
        itemRegisterEntryId = byteBuf.readInt();
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.setItemOnGroundPos(uuid, itemRegisterEntryId, worldPosX, worldPosY);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
        byteBuf.writeFloat(worldPosX);
        byteBuf.writeFloat(worldPosY);
        byteBuf.writeInt(itemRegisterEntryId);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2 + Float.BYTES * 2 + Integer.BYTES;
    }
}
