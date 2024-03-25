package ch.realmtech.server.packet.serverPacket;

import ch.realmtech.server.packet.ServerPacket;
import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class PlayerWeaponShotPacket implements ServerPacket {
    private final Vector2 dst;

    public PlayerWeaponShotPacket(float dstX, float dstY) {
        dst = new Vector2(dstX, dstY);
    }

    public PlayerWeaponShotPacket(Vector2 dst) {
        this.dst = dst;
    }

    public PlayerWeaponShotPacket(ByteBuf byteBuf) {
        float dstX = byteBuf.readFloat();
        float dstY = byteBuf.readFloat();
        dst = new Vector2(dstX, dstY);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(dst.x);
        byteBuf.writeFloat(dst.y);
    }

    @Override
    public int getSize() {
        return Float.BYTES * 2;
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.playerWeaponShot(clientChannel, new Vector2(dst.x, dst.y));
    }
}
