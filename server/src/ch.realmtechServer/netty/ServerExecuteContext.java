package ch.realmtechServer.netty;

import ch.realmtechCommuns.ecs.component.PlayerConnectionComponent;
import ch.realmtechCommuns.ecs.system.PlayerManagerServer;
import ch.realmtechCommuns.packet.ServerResponseHandler;
import ch.realmtechCommuns.packet.clientPacket.ConnectionAutreJoueurPacket;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import ch.realmtechCommuns.packet.serverPacket.ServerExecute;
import ch.realmtechServer.ServerContext;
import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;

import java.util.List;

public class ServerExecuteContext implements ServerExecute {
    private final ServerContext serverContext;

    public ServerExecuteContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void createPlayer(Channel clientChanel, ServerResponseHandler serverResponseHandler) {
        List<PlayerConnectionComponent> playersConnection = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getPlayersConnection();
        Vector2 playerPos = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).createPlayerServer(clientChanel);
        float[] playersPos = new float[playersConnection.size() * 2];
        for (int i = 0, j = 0; i < playersConnection.size(); i += 2, j++) {
            float[] autrePlayerPos = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getPlayerConnectionPos(playersConnection.get(j));
            playersPos[i] = autrePlayerPos[0];
            playersPos[i + 1] = autrePlayerPos[1];
        }
        serverResponseHandler.sendPacketTo(new ConnectionJoueurReussitPacket(playerPos.x, playerPos.y, playersConnection.size(), playersPos), clientChanel);
        serverResponseHandler.boardCastPacketExcept(new ConnectionAutreJoueurPacket(playerPos.x, playerPos.y), clientChanel);
    }
}
