package ch.realmtechServer.netty;

import ch.realmtechCommuns.packet.clientPacket.ConnexionJoueurReussitPacket;
import ch.realmtechCommuns.packet.serverPacket.ServerExecute;
import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ecs.system.PlayerManagerServer;
import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;

public class ServerExecuteContext implements ServerExecute {
    private final ServerContext serverContext;

    public ServerExecuteContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void newPlayerConnect(Channel clientChanel) {
        // connexion réussie
        ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg connexionJoueurReussitArg = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).createPlayerServer(clientChanel);
        serverContext.getServerHandler().sendPacketTo(new ConnexionJoueurReussitPacket(connexionJoueurReussitArg.x(), connexionJoueurReussitArg.y(), connexionJoueurReussitArg.uuid()), clientChanel);

//        // tous les joueurs
//        PlayerManagerServer.TousLesJoueursArg tousLesJoueursArgs = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getTousLesJoueurs();
//        serverContext.getServerHandler().sendPacketTo(new TousLesJoueurPacket(tousLesJoueursArgs.nombreDeJoueur(), tousLesJoueursArgs.pos(), tousLesJoueursArgs.uuids()), clientChanel);
    }

    @Override
    public void removePlayer(Channel channel) {
        serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).removePlayer(channel);
    }

    @Override
    public void playerMove(Channel clientChannel, float impulseX, float impulseY, Vector2 pos) {
        serverContext.nextTick(() -> {
            serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).playerMove(clientChannel, impulseX, impulseY, pos);
        });
    }
}
