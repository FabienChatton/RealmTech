package ch.realmtechServer.netty;

import ch.realmtechCommuns.ecs.component.PlayerConnectionComponent;
import ch.realmtechCommuns.ecs.component.PositionComponent;
import ch.realmtechCommuns.ecs.system.PlayerManagerServer;
import ch.realmtechCommuns.packet.ServerResponseHandler;
import ch.realmtechCommuns.packet.clientPacket.ConnectionAutreJoueurPacket;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import ch.realmtechCommuns.packet.clientPacket.TousLesJoueurPacket;
import ch.realmtechCommuns.packet.serverPacket.ServerExecute;
import ch.realmtechServer.ServerContext;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;

import java.util.UUID;

public class ServerExecuteContext implements ServerExecute {
    private final ServerContext serverContext;

    public ServerExecuteContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void newPlayerConnect(Channel clientChanel, ServerResponseHandler serverResponseHandler) {
        // connection réussie
        ConnectionJoueurReussitPacket.ConnectionJoueurReussitArg connectionJoueurReussitArg = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).createPlayerServer(clientChanel);
        serverResponseHandler.sendPacketTo(new ConnectionJoueurReussitPacket(connectionJoueurReussitArg.x(), connectionJoueurReussitArg.y(), connectionJoueurReussitArg.uuid()), clientChanel);

        // tous les joueurs
        IntBag players = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getPlayers();
        int[] playersData = players.getData();
        Vector2[] poss = new Vector2[players.size()];
        UUID[] uuids = new UUID[players.size()];
        ComponentMapper<PlayerConnectionComponent> mPlayer = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnectionComponent.class);
        ComponentMapper<PositionComponent> mPos = serverContext.getEcsEngineServer().getWorld().getMapper(PositionComponent.class);
        for (int i = 0; i < players.size(); i++) {
            PositionComponent positionComponent = mPos.get(playersData[i]);
            PlayerConnectionComponent playerComponent = mPlayer.get(playersData[i]);
            poss[i] = new Vector2(positionComponent.x, positionComponent.y);
            uuids[i] = playerComponent.uuid;
        }
        serverResponseHandler.sendPacketTo(new TousLesJoueurPacket(players.size(), poss, uuids), clientChanel);

        // autre, ajoute joueur
        serverResponseHandler.boardCastPacketExcept(new ConnectionAutreJoueurPacket(connectionJoueurReussitArg.x(), connectionJoueurReussitArg.y(), connectionJoueurReussitArg.uuid()), clientChanel);
    }

    @Override
    public void removePlayer(Channel channel) {
        serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).removePlayer(channel);
    }
}
