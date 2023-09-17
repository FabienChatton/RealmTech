package ch.realmtechCommuns.packet.serverPacket;

import ch.realmtechCommuns.packet.ServerResponseHandler;
import io.netty.channel.Channel;

public interface ServerExecute {
    void createPlayer(Channel clientChanel, ServerResponseHandler serverResponseHandler);
}
