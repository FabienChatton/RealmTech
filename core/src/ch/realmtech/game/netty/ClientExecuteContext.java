package ch.realmtech.game.netty;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.ItemManagerClient;
import ch.realmtech.game.ecs.system.PlayerManagerClient;
import ch.realmtech.helper.Popup;
import ch.realmtech.screen.ScreenType;
import ch.realmtechServer.ecs.component.InfCellComponent;
import ch.realmtechServer.ecs.component.InfMapComponent;
import ch.realmtechServer.ecs.system.MapManager;
import ch.realmtechServer.level.cell.BreakCell;
import ch.realmtechServer.packet.ClientPacket;
import ch.realmtechServer.packet.clientPacket.ClientExecute;
import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;

public class ClientExecuteContext implements ClientExecute {
    private final static Logger logger = LoggerFactory.getLogger(ClientExecuteContext.class);
    private final RealmTech context;

    public ClientExecuteContext(RealmTech context) {
        this.context = context;
    }

    @Override
    public void connexionJoueurReussit(float x, float y, UUID uuid) {
        context.getEcsEngine().getSystem(PlayerManagerClient.class).createPlayerClient(x, y, uuid);
        Gdx.app.postRunnable(() -> context.setScreen(ScreenType.GAME_SCREEN));
    }

    @Override
    public void autreJoueur(float x, float y, UUID uuid) {
        context.nextFrame(() -> {
            HashMap<UUID, Integer> players = context.getEcsEngine().getSystem(PlayerManagerClient.class).getPlayers();
            if (!players.containsKey(uuid)) {
                context.getEcsEngine().getSystem(PlayerManagerClient.class).createPlayerClient(x, y, uuid);
            }
            context.getEcsEngine().getSystem(PlayerManagerClient.class).setPlayerPos(x, y, uuid);
        });
    }

    @Override
    public void chunkAMounter(int chunkPosX, int chunkPosY, byte[] chunkBytes) {
//        logger.debug("chunk à mounter {},{}", chunkPosX, chunkPosY);
        context.nextFrame(() -> context.getEcsEngine().getSystem(MapManager.class).chunkAMounter(chunkPosX, chunkPosY, chunkBytes));
    }

    @Override
    public void chunkADamner(int chunkPosX, int chunkPosY) {
//        logger.debug("chunk à damner {},{}", chunkPosX, chunkPosY);
        context.nextFrame(() -> context.getEcsEngine().getSystem(MapManager.class).damneChunkClient(chunkPosX, chunkPosY));
    }

    @Override
    public void chunkARemplacer(int chunkPosX, int chunkPosY, byte[] chunkBytes, int oldChunkPosX, int oldChunkPosY) {
//        logger.debug("chunk à remplacer old {},{}. new {},{} ", oldChunkPosX, oldChunkPosY, chunkPosX, chunkPosY);
        context.nextFrame(() -> context.getEcsEngine().getSystem(MapManager.class).chunkARemplacer(chunkPosX, chunkPosY, chunkBytes, oldChunkPosX, oldChunkPosY));
    }

    @Override
    public void deconnectionJoueur(UUID uuid) {
        context.nextFrame(() -> context.getEcsEngine().getSystem(PlayerManagerClient.class).removePlayer(uuid));
    }

    @Override
    public void clientConnexionRemoved() {
        Gdx.app.postRunnable(() -> {
            if (context.getScreenType() == ScreenType.GAME_SCREEN) {
                Gdx.app.postRunnable(() -> {
                    context.setScreen(ScreenType.MENU);
                    Popup.popupErreur(context, "Le serveur est fermé", context.getUiStage());
                });
            }
        });
    }

    @Override
    public void cellBreak(int chunkPosX, int chunkPosY, byte innerChunkX, byte innerChunkY, UUID playerUUID, int itemUsedByPlayerHash) {
        context.nextFrame(() -> {
            int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
            int chunkId = context.getSystem(MapManager.class).getChunk(chunkPosX, chunkPosY, infChunks);
            int cellId = context.getSystem(MapManager.class).getTopCell(chunkId, innerChunkX, innerChunkY);
            int playerId = context.getSystem(PlayerManagerClient.class).getPlayers().get(playerUUID);
            ComponentMapper<InfCellComponent> mCell = context.getEcsEngine().getWorld().getMapper(InfCellComponent.class);
            BreakCell breakCellEvent = mCell.get(cellId).cellRegisterEntry.getCellBehavior().getBreakCellEvent();
            if (breakCellEvent != null) breakCellEvent.breakCell(context.getEcsEngine().getSystem(ItemManagerClient.class), context.getEcsEngine().getWorld(), chunkId, cellId, ItemRegisterEntry.getItemByHash(itemUsedByPlayerHash));
        });
    }

    @Override
    public void tickBeat(float tickElapseTime) {
        context.getEcsEngine().serverTickBeatMonitoring.addTickElapseTime(tickElapseTime);
    }

    @Override
    public <T extends ClientPacket> void packetReciveMonitoring(T packet) {
        context.getEcsEngine().serverTickBeatMonitoring.addPacketResive(packet);
    }

    @Override
    public void setPlayerInventory(UUID playerUUID, byte[] inventoryBytes) {
        context.nextFrame(() -> {
            context.getSystem(PlayerManagerClient.class).setPlayerInventory(playerUUID, inventoryBytes);
            context.getEcsEngine().togglePlayerInventoryWindow();
        });
    }
}
