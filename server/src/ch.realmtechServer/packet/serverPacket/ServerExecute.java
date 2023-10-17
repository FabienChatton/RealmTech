package ch.realmtechServer.packet.serverPacket;

import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;

public interface ServerExecute {
    void newPlayerConnect(Channel clientChanel);

    void removePlayer(Channel channel);

    void playerMove(Channel clientChannel, float impulseX, float impulseY, Vector2 pos);

    void cellBreakRequest(Channel clientChannel, int chunkPosX, int chunkPosY, byte innerChunkX, byte innerChunkY, int itemUseByPlayerHash);

    void getPlayerInventorySession(Channel clientChannel);
}
