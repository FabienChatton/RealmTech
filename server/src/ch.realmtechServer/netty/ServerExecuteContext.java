package ch.realmtechServer.netty;

import ch.realmtechServer.ecs.system.PlayerManagerServer;
import ch.realmtechCommuns.packet.clientPacket.ConnectionJoueurReussitPacket;
import ch.realmtechCommuns.packet.clientPacket.TousLesJoueurPacket;
import ch.realmtechCommuns.packet.serverPacket.ServerExecute;
import ch.realmtechServer.ServerContext;
import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;

public class ServerExecuteContext implements ServerExecute {
    private final ServerContext serverContext;

    public ServerExecuteContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void newPlayerConnect(Channel clientChanel) {
        // connection réussie
        ConnectionJoueurReussitPacket.ConnectionJoueurReussitArg connectionJoueurReussitArg = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).createPlayerServer(clientChanel);
        serverContext.getServerHandler().sendPacketTo(new ConnectionJoueurReussitPacket(connectionJoueurReussitArg.x(), connectionJoueurReussitArg.y(), connectionJoueurReussitArg.uuid()), clientChanel);

        // tous les joueurs
        PlayerManagerServer.TousLesJoueursArg tousLesJoueursArgs = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getTousLesJoueurs();
        serverContext.getServerHandler().sendPacketTo(new TousLesJoueurPacket(tousLesJoueursArgs.nombreDeJoueur(), tousLesJoueursArgs.pos(), tousLesJoueursArgs.uuids()), clientChanel);
    }

    @Override
    public void removePlayer(Channel channel) {
        serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).removePlayer(channel);
    }

    @Override
    public void playerMove(Channel clientChannel, float impulseX, float impulseY, Vector2 pos) {
        serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).playerMove(clientChannel, impulseX, impulseY, pos);
    }
}
