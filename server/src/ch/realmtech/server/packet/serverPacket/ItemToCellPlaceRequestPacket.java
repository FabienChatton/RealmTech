package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ServerPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.UUID;

public class ItemToCellPlaceRequestPacket implements ServerPacket {
    private final UUID itemToPlaceUuid;
    private final int worldX;
    private final int worldY;

    public ItemToCellPlaceRequestPacket(UUID itemToPlaceUuid, int worldX, int worldY) {
        this.itemToPlaceUuid = itemToPlaceUuid;
        this.worldX = worldX;
        this.worldY = worldY;
    }

    public ItemToCellPlaceRequestPacket(ByteBuf byteBuf) {
        itemToPlaceUuid = ByteBufferHelper.readUUID(byteBuf);
        worldX = byteBuf.readInt();
        worldY = byteBuf.readInt();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteBufferHelper.writeUUID(byteBuf, itemToPlaceUuid);
        byteBuf.writeInt(worldX);
        byteBuf.writeInt(worldY);
    }

    @Override
    public int getSize() {
        return Long.BYTES * 2 + Integer.BYTES * 2;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.itemToCellPlace(clientChannel, itemToPlaceUuid, worldX, worldY);
    }
}
