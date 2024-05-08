package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.divers.ByteBufferHelper;
import ch.realmtech.server.packet.ServerPacket;
import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.util.UUID;

public class PlayerWeaponShotPacket implements ServerPacket {
    private final Vector2 vectorClick;
    private final UUID itemUuid;

    public PlayerWeaponShotPacket(float dstX, float dstY, UUID itemUuid) {
        vectorClick = new Vector2(dstX, dstY);
        this.itemUuid = itemUuid;
    }

    public PlayerWeaponShotPacket(ByteBuf byteBuf) {
        float dstX = byteBuf.readFloat();
        float dstY = byteBuf.readFloat();
        vectorClick = new Vector2(dstX, dstY);

        itemUuid = ByteBufferHelper.readUUID(byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(vectorClick.x);
        byteBuf.writeFloat(vectorClick.y);
        ByteBufferHelper.writeUUID(byteBuf, itemUuid);
    }

    @Override
    public int getSize() {
        return Float.BYTES * 2 + Long.BYTES * 2;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.playerWeaponShot(clientChannel, new Vector2(vectorClick.x, vectorClick.y), itemUuid);
    }
}
