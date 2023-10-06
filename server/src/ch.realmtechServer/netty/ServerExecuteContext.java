package ch.realmtechServer.netty;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ecs.component.InfMapComponent;
import ch.realmtechServer.ecs.component.PlayerConnexionComponent;
import ch.realmtechServer.ecs.system.MapManager;
import ch.realmtechServer.ecs.system.PlayerManagerServer;
import ch.realmtechServer.packet.clientPacket.CellBreakPacket;
import ch.realmtechServer.packet.clientPacket.ConnexionJoueurReussitPacket;
import ch.realmtechServer.packet.clientPacket.DeconnectionJoueurPacket;
import ch.realmtechServer.packet.serverPacket.ServerExecute;
import ch.realmtechServer.registery.ItemRegisterEntry;
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
        PlayerConnexionComponent playerConnexionComponentChannel = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getPlayerConnexionComponentByChannel(channel);
        serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).removePlayer(channel);
        serverContext.getServerHandler().broadCastPacketExcept(new DeconnectionJoueurPacket(playerConnexionComponentChannel.uuid), channel);
    }

    @Override
    public void playerMove(Channel clientChannel, float impulseX, float impulseY, Vector2 pos) {
        serverContext.nextTick(() -> {
            serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).playerMove(clientChannel, impulseX, impulseY, pos);
        });
    }

    @Override
    public void cellBreakRequest(Channel clientChannel, int chunkPosX, int chunkPosY, byte innerChunkX, byte innerChunkY, int itemUseByPlayerHash) {
        serverContext.nextTick(() -> {
            int playerId = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getPlayerByChannel(clientChannel);
            PlayerConnexionComponent playerConnexionComponent = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getPlayerConnexionComponentByChannel(clientChannel);
            int[] infChunks = playerConnexionComponent.infChunks;
            int chunkId = serverContext.getEcsEngineServer().getWorld().getSystem(MapManager.class).getChunk(chunkPosX, chunkPosY, infChunks);
            int cellId = serverContext.getEcsEngineServer().getWorld().getSystem(MapManager.class).getTopCell(chunkId, innerChunkX, innerChunkY);
            if (serverContext.getEcsEngineServer().getWorld().getSystem(MapManager.class).breakCellServer(chunkId, cellId, playerId, ItemRegisterEntry.getItemByHash(itemUseByPlayerHash))) {
                serverContext.getServerHandler().broadCastPacket(new CellBreakPacket(chunkPosX, chunkPosY, innerChunkX, innerChunkY, playerConnexionComponent.uuid, itemUseByPlayerHash));
            }
        });
    }
}
