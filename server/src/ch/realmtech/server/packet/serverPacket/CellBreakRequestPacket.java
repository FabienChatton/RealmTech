package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ServerPacket;
import com.badlogic.gdx.utils.Null;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.UUID;

public class CellBreakRequestPacket implements ServerPacket {
    private final int worldPosX;
    private final int worldPosY;
    private final UUID itemUsedUuid;

    public CellBreakRequestPacket(int worldPosX, int worldPosY, @Null UUID itemUsedUuid) {
        this.worldPosX = worldPosX;
        this.worldPosY = worldPosY;
        this.itemUsedUuid = itemUsedUuid;
    }

    public CellBreakRequestPacket(ByteBuf byteBuf) {
        worldPosX = byteBuf.readInt();
        worldPosY = byteBuf.readInt();
        boolean hasItemUsed = byteBuf.readBoolean();
        if (hasItemUsed) {
            itemUsedUuid = ByteBufferHelper.readUUID(byteBuf);
        } else {
            itemUsedUuid = null;
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeInt(worldPosX);
        byteBuf.writeInt(worldPosY);
        if (itemUsedUuid != null) {
            byteBuf.writeBoolean(true);
            ByteBufferHelper.writeUUID(byteBuf, itemUsedUuid);
        } else {
            byteBuf.writeBoolean(false);
        }
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.cellBreakRequest(clientChannel, worldPosX, worldPosY, itemUsedUuid);
    }

    @Override
    public int getSize() {
        return Integer.SIZE * 2 + Long.BYTES * 2;
    }
}
