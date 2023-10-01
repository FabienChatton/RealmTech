package ch.realmtechCommuns.packet.serverPacket;

import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;

public interface ServerExecute {
    void newPlayerConnect(Channel clientChanel);

    void removePlayer(Channel channel);

    void playerMove(Channel clientChannel, float impulseX, float impuseY, Vector2 pos);
}
