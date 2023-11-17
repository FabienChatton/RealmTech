package ch.realmtech.server.netty;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.system.*;
import ch.realmtech.server.level.cell.BreakCell;
import ch.realmtech.server.packet.clientPacket.ConnexionJoueurReussitPacket;
import ch.realmtech.server.packet.clientPacket.DeconnectionJoueurPacket;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import ch.realmtech.server.packet.clientPacket.WriteToConsolePacket;
import ch.realmtech.server.packet.serverPacket.ServerExecute;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.serialize.inventory.InventorySerializer;
import com.artemis.ComponentMapper;
import io.netty.channel.Channel;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ServerExecuteContext implements ServerExecute {
    private final ServerContext serverContext;

    public ServerExecuteContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public void newPlayerConnect(Channel clientChanel) {
        // connexion réussie
        ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg connexionJoueurReussitArg = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).createPlayerServer(clientChanel);
        serverContext.getServerHandler().sendPacketTo(new ConnexionJoueurReussitPacket(connexionJoueurReussitArg), clientChanel);

//        // tous les joueurs
//        PlayerManagerServer.TousLesJoueursArg tousLesJoueursArgs = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getTousLesJoueurs();
//        serverContext.getServerHandler().sendPacketTo(new TousLesJoueurPacket(tousLesJoueursArgs.nombreDeJoueur(), tousLesJoueursArgs.pos(), tousLesJoueursArgs.uuids()), clientChanel);
    }

    @Override
    public void removePlayer(Channel channel) {
        int playerId = serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).getPlayerByChannel(channel);
        serverContext.getEcsEngineServer().getWorld().getSystem(PlayerManagerServer.class).removePlayer(channel);
        serverContext.getServerHandler().broadCastPacketExcept(new DeconnectionJoueurPacket(serverContext.getSystem(UuidComponentManager.class).getRegisteredComponent(playerId).getUuid()), channel);
    }

    @Override
    public void playerMove(Channel clientChannel, byte inputKeys) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            serverContext.getSystem(PlayerMouvementSystemServer.class).playerMove(clientChannel, inputKeys);
        });
    }

    @Override
    public void cellBreakRequest(Channel clientChannel, int worldPosX, int worldPosY, int itemUseByPlayerHash) {
        serverContext.getEcsEngineServer().nextTick(() -> {
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
        serverContext.getEcsEngineServer().nextTick(() -> {
            int playerId = serverContext.getSystem(PlayerManagerServer.class).getPlayerByChannel(clientChannel);
            ComponentMapper<UuidComponent> mUuid = serverContext.getEcsEngineServer().getWorld().getMapper(UuidComponent.class);
            getInventory(clientChannel, mUuid.get(serverContext.getSystem(InventoryManager.class).getChestInventoryId(playerId)).getUuid());
        });
    }

    @Override
    public void consoleCommande(Channel clientChannel, String stringCommande) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PrintWriter printWriter = new PrintWriter(baos, true, StandardCharsets.US_ASCII)) {
                serverContext.getCommandeExecute().execute(stringCommande, printWriter);
            }
            serverContext.getServerHandler().sendPacketTo(new WriteToConsolePacket(baos.toString()), clientChannel);
        });
    }

    @Override
    public void moveStackToStackNumberRequest(Channel clientChannel, UUID srcInventory, UUID dstInventory, UUID[] itemsToMove, int slotIndex) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            serverContext.getSystem(InventoryManager.class).moveStackToStackRequest(srcInventory, dstInventory, itemsToMove, slotIndex);
        });
    }

    @Override
    public void getInventory(Channel clientChannel, UUID inventoryUuid) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            InventoryComponent inventoryComponent = serverContext.getSystem(InventoryManager.class).getInventoryComponentByUUID(inventoryUuid);
            byte[] bytes = InventorySerializer.toBytes(serverContext.getEcsEngineServer().getWorld(), inventoryComponent);
            clientChannel.writeAndFlush(new InventorySetPacket(inventoryUuid, bytes));
        });
    }
}
