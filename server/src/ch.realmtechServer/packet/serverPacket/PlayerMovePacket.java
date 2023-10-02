package ch.realmtechServer.packet.serverPacket;

import ch.realmtechServer.packet.ServerPacket;
import com.badlogic.gdx.math.Vector2;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public class PlayerMovePacket implements ServerPacket {
    private final float impulseX;
    private final float impulseY;
    private final Vector2 pos;

    public PlayerMovePacket(float impulseX, float impulseY, Vector2 pos) {
        this.impulseX = impulseX;
        this.impulseY = impulseY;
        this.pos = pos;
    }

    public PlayerMovePacket(ByteBuf byteBuf) {
        this.impulseX = byteBuf.readFloat();
        this.impulseY = byteBuf.readFloat();
        float x = byteBuf.readFloat();
        float y = byteBuf.readFloat();
        this.pos = new Vector2(x, y);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeFloat(impulseX);
        byteBuf.writeFloat(impulseY);
        byteBuf.writeFloat(pos.x);
        byteBuf.writeFloat(pos.y);
    }

    @Override
    public void executeOnServer(Channel clientChannel, ServerExecute serverExecute) {
        serverExecute.playerMove(clientChannel, impulseX, impulseY, pos);
    }
}
