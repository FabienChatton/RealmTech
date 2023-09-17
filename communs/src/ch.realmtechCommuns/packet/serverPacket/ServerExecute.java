package ch.realmtechCommuns.packet.serverPacket;

import ch.realmtechCommuns.packet.ServerResponseHandler;
import io.netty.channel.Channel;

public interface ServerExecute {
    void newPlayerConnect(Channel clientChanel, ServerResponseHandler serverResponseHandler);

    void removePlayer(Channel channel);
}
