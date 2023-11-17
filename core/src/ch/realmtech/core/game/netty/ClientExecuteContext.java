package ch.realmtech.core.game.netty;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.system.ItemManagerClient;
import ch.realmtech.core.game.ecs.system.PlayerInventorySystem;
import ch.realmtech.core.game.ecs.system.PlayerManagerClient;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.core.screen.ScreenType;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.packet.ClientPacket;
import ch.realmtech.server.packet.clientPacket.ClientExecute;
import ch.realmtech.server.packet.clientPacket.ConnexionJoueurReussitPacket;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.serialize.inventory.InventorySerializer;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class ClientExecuteContext implements ClientExecute {
    private final static Logger logger = LoggerFactory.getLogger(ClientExecuteContext.class);
    private final RealmTech context;

    public ClientExecuteContext(RealmTech context) {
        this.context = context;
    }

    @Override
    public void connexionJoueurReussit(ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg connexionJoueurReussitArg) {
        int playerId = context.getEcsEngine().getSystem(PlayerManagerClient.class).createPlayerClient(connexionJoueurReussitArg.x(), connexionJoueurReussitArg.y(), connexionJoueurReussitArg.playerUuid());
        PlayerConnexionComponent playerConnexionComponent = context.getEcsEngine().getWorld().getMapper(PlayerConnexionComponent.class).get(playerId);

        World world = context.getEcsEngine().getWorld();
        Function<ItemManager, int[][]> inventoryFromBytes = InventorySerializer.getFromBytes(world, connexionJoueurReussitArg.inventoryBytes());
        // inventory chest
        context.getSystem(InventoryManager.class).createChest(playerId, inventoryFromBytes.apply(context.getSystem(ItemManager.class)), connexionJoueurReussitArg.inventoryUuid(), InventoryComponent.DEFAULT_NUMBER_OF_SLOT_PAR_ROW, InventoryComponent.DEFAULT_NUMBER_OF_ROW);
        // crafting table
        context.getSystem(InventoryManager.class).createCraftingTable(playerId, connexionJoueurReussitArg.inventoryCraftUuid(), 2, 2, connexionJoueurReussitArg.inventoryCraftResultUuid());
        // inventory cursor
        context.getSystem(InventoryManager.class).createCursorInventory(playerId, connexionJoueurReussitArg.inventoryCursorUuid(), 1, 1);

        Gdx.app.postRunnable(() -> {
            context.getSystem(PlayerInventorySystem.class).createClickAndDrop(playerId);
            context.setScreen(ScreenType.GAME_SCREEN);
        });
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


    public void cellBreak(int worldPosX, int worldPosY) {
        context.nextFrame(() -> {
            int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
            int chunkPosX = MapManager.getChunkPos(worldPosX);
            int chunkPosY = MapManager.getChunkPos(worldPosY);
            int chunkId = context.getSystem(MapManager.class).getChunk(chunkPosX, chunkPosY, infChunks);
            int cellId = context.getSystem(MapManager.class).getTopCell(chunkId, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
            context.getSystem(MapManager.class).damneCell(chunkId, cellId);
        });
    }

    @Override
    public void tickBeat(float tickElapseTime) {
        context.nextFrame(() -> context.getEcsEngine().serverTickBeatMonitoring.addTickElapseTime(tickElapseTime));
    }

    @Override
    public <T extends ClientPacket> void packetReciveMonitoring(T packet) {
        context.nextFrame(() -> context.getEcsEngine().serverTickBeatMonitoring.addPacketResive(packet));
    }

    @Override
    public void setInventory(UUID inventoryUUID, byte[] inventoryBytes) {
        context.nextFrame(() -> {
            context.getSystem(InventoryManager.class).setInventory(inventoryUUID, inventoryBytes);
            ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = context.getEcsEngine().getWorld().getMapper(PlayerConnexionComponent.class);
            ComponentMapper<UuidComponent> mUuid = context.getEcsEngine().getWorld().getMapper(UuidComponent.class);
            int mainInventoryId = context.getSystem(InventoryManager.class).getChestInventoryId(context.getSystem(PlayerManagerClient.class).getMainPlayer());
            if (mUuid.get(mainInventoryId).getUuid().equals(inventoryUUID)) {
                context.getEcsEngine().getSystem(PlayerInventorySystem.class).refreshInventory(context.getEcsEngine().getSystem(PlayerInventorySystem.class).getDisplayInventoryPlayer().apply(context));
            }
        });
    }

    @Override
    public void setItemOnGroundPos(UUID uuid, ItemRegisterEntry itemRegisterEntry, float worldPosX, float worldPosY) {
        context.nextFrame(() -> context.getSystem(ItemManagerClient.class).setItemOnGroundPos(uuid, itemRegisterEntry, worldPosX, worldPosY));
    }

    @Override
    public void supprimeItemOnGround(UUID itemUuid) {
        context.nextFrame(() -> context.getSystem(ItemManagerClient.class).supprimeItemOnGround(itemUuid));
    }

    @Override
    public void writeOnConsoleMessage(String consoleMessageToWrite) {
        context.nextFrame(() -> context.writeToConsole(consoleMessageToWrite));
    }
}
