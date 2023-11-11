package ch.realmtech.server.packet.clientPacket;

import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.registery.ItemRegisterEntry;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ItemOnGroundPacket implements ClientPacket {

    private final UUID uuid;
    private final float worldPosX;
    private final float worldPosY;
    private final ItemRegisterEntry itemRegisterEntry;

    public ItemOnGroundPacket(UUID uuid, ItemRegisterEntry itemRegisterEntry, float worldPosX, float worldPosY) {
        this.uuid = uuid;
        this.worldPosX = worldPosX;
        this.worldPosY = worldPosY;
        this.itemRegisterEntry = itemRegisterEntry;
    }

    public ItemOnGroundPacket(ByteBuf byteBuf) {
        long msb = byteBuf.readLong();
        long lsb = byteBuf.readLong();
        uuid = new UUID(msb, lsb);
        worldPosX = byteBuf.readFloat();
        worldPosY = byteBuf.readFloat();
        itemRegisterEntry = ItemRegisterEntry.getItemByHash(byteBuf.readInt());
    }

    @Override
    public void executeOnClient(ClientExecute clientExecute) {
        clientExecute.setItemOnGroundPos(uuid, itemRegisterEntry, worldPosX, worldPosY);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
        byteBuf.writeFloat(worldPosX);
        byteBuf.writeFloat(worldPosY);
        byteBuf.writeInt(ItemRegisterEntry.getHash(itemRegisterEntry));
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2 + Float.BYTES * 2 + Integer.BYTES;
    }
}
