package ch.realmtechServer.netty;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ecs.component.InfCellComponent;
import ch.realmtechServer.ecs.component.InfMapComponent;
import ch.realmtechServer.ecs.component.InventoryComponent;
import ch.realmtechServer.ecs.component.PlayerConnexionComponent;
import ch.realmtechServer.ecs.system.MapManager;
import ch.realmtechServer.ecs.system.MapSystemServer;
import ch.realmtechServer.ecs.system.PlayerManagerServer;
import ch.realmtechServer.ecs.system.PlayerMouvementSystemServer;
import ch.realmtechServer.level.cell.BreakCell;
import ch.realmtechServer.packet.clientPacket.ConnexionJoueurReussitPacket;
import ch.realmtechServer.packet.clientPacket.DeconnectionJoueurPacket;
import ch.realmtechServer.packet.clientPacket.PlayerInventoryPacket;
import ch.realmtechServer.packet.clientPacket.WriteToConsolePacket;
import ch.realmtechServer.packet.serverPacket.ServerExecute;
import ch.realmtechServer.registery.ItemRegisterEntry;
import ch.realmtechServer.serialize.inventory.InventorySerializer;
import com.artemis.ComponentMapper;
import io.netty.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

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
    public void playerMove(Channel clientChannel, byte inputKeys) {
        ServerContext.nextTick(() -> {
            serverContext.getSystem(PlayerMouvementSystemServer.class).playerMove(clientChannel, inputKeys);
        });
    }

    @Override
    public void cellBreakRequest(Channel clientChannel, int worldPosX, int worldPosY, int itemUseByPlayerHash) {
        ServerContext.nextTick(() -> {
            int playerId = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getPlayerByChannel(clientChannel);
            PlayerConnexionComponent playerConnexionComponent = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getPlayerConnexionComponentByChannel(clientChannel);
            InfMapComponent infMapComponent = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class);
            int[] infChunks = infMapComponent.infChunks;
            int chunkId = serverContext.getEcsEngineServer().getWorld().getSystem(MapManager.class).getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infChunks);
            int cellId = serverContext.getEcsEngineServer().getWorld().getSystem(MapManager.class).getTopCell(chunkId, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
            ComponentMapper<InfCellComponent> mCell = serverContext.getEcsEngineServer().getWorld().getMapper(InfCellComponent.class);
            InfCellComponent infCellComponent = mCell.get(cellId);
            BreakCell breakCellEvent = infCellComponent.cellRegisterEntry.getCellBehavior().getBreakCellEvent();
            if (breakCellEvent != null) {
                breakCellEvent.breakCell(serverContext.getSystem(MapSystemServer.class), serverContext.getEcsEngineServer().getWorld(), chunkId, cellId, ItemRegisterEntry.getItemByHash(itemUseByPlayerHash));
            }
        });
    }

    @Override
    public void getPlayerInventorySession(Channel clientChannel) {
        ServerContext.nextTick(() -> {
            int playerId = serverContext.getSystem(PlayerManagerServer.class).getPlayerByChannel(clientChannel);
            PlayerConnexionComponent playerConnexionComponent = serverContext.getSystem(PlayerManagerServer.class).getPlayerConnexionComponentByChannel(clientChannel);
            InventoryComponent inventoryComponent = serverContext.getEcsEngineServer().getWorld().getMapper(InventoryComponent.class).get(playerId);
            byte[] bytes = InventorySerializer.toBytes(serverContext.getEcsEngineServer().getWorld(), inventoryComponent);
            clientChannel.writeAndFlush(new PlayerInventoryPacket(bytes, playerConnexionComponent.uuid));
        });
    }

    @Override
    public void consoleCommande(Channel clientChannel, String stringCommande) {
        ServerContext.nextTick(() -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PrintWriter printWriter = new PrintWriter(baos, true, StandardCharsets.US_ASCII)) {
                serverContext.getCommandeExecute().execute(stringCommande, printWriter);
            }
            serverContext.getServerHandler().sendPacketTo(new WriteToConsolePacket(baos.toString()), clientChannel);
        });
    }
}
