package ch.realmtech.core.game.netty;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.core.screen.ScreenType;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ecs.component.InfMapComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.packet.clientPacket.ClientExecute;
import ch.realmtech.server.packet.clientPacket.ConnexionJoueurReussitPacket;
import ch.realmtech.server.packet.clientPacket.ParticleAddPacket;
import ch.realmtech.server.serialize.cell.CellArgs;
import ch.realmtech.server.serialize.inventory.InventoryArgs;
import ch.realmtech.server.serialize.physicEntity.PhysicEntityArgs;
import ch.realmtech.server.serialize.physicEntity.PhysicEntitySerializerController;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class ClientExecuteContext implements ClientExecute {
    private final static Logger logger = LoggerFactory.getLogger(ClientExecuteContext.class);
    private final RealmTech context;

    public ClientExecuteContext(RealmTech context) {
        this.context = context;
    }

    @Override
    public ClientContext getContext() {
        return context;
    }

    @Override
    public void connexionJoueurReussit(ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg connexionJoueurReussitArg) {
        Gdx.app.postRunnable(() -> context.setScreen(ScreenType.GAME_SCREEN));
        context.nextFrame(() -> {
            int playerId = context.getSystemsAdminClient().getPlayerManagerClient().createPlayerClient(connexionJoueurReussitArg.playerUuid());
            context.getSystemsAdminClient().getPlayerManagerClient().playerInRange(connexionJoueurReussitArg.x(), connexionJoueurReussitArg.y(), connexionJoueurReussitArg.playerUuid());
            PlayerConnexionComponent playerConnexionComponent = context.getEcsEngine().getWorld().getMapper(PlayerConnexionComponent.class).get(playerId);

            World world = context.getEcsEngine().getWorld();
            Function<ItemManager, InventoryArgs> inventoryGet = context.getSerializerController().getInventorySerializerManager().decode(connexionJoueurReussitArg.applicationInventoryBytes());
            // inventory chest
            InventoryArgs inventoryArgs = inventoryGet.apply(context.getWorld().getRegistered("itemManager"));
            context.getSystemsAdminClient().getInventoryManager().createChest(playerId, inventoryArgs.inventory(), connexionJoueurReussitArg.inventoryUuid(), inventoryArgs.numberOfSlotParRow(), inventoryArgs.numberOfRow());
            // crafting table
            context.getSystemsAdminClient().getInventoryManager().createCraftingTable(playerId, connexionJoueurReussitArg.inventoryCraftUuid(), 2, 2, connexionJoueurReussitArg.inventoryCraftResultUuid());
            // inventory cursor
            context.getSystemsAdminClient().getInventoryManager().createCursorInventory(playerId, connexionJoueurReussitArg.inventoryCursorUuid(), 1, 1);

            context.getSystemsAdminClient().getPlayerInventorySystem().createClickAndDrop(playerId);
        });
    }

    @Override
    public void chunkAMounter(SerializedApplicationBytes applicationChunkBytes) {
        context.nextFrame(() -> context.getSystemsAdminClient().getMapManager().chunkAMounter(applicationChunkBytes));
    }

    @Override
    public void chunkADamner(int chunkPosX, int chunkPosY) {
        context.nextFrame(() -> context.getSystemsAdminClient().getMapManager().damneChunkClient(chunkPosX, chunkPosY));
    }

    @Override
    public void chunkARemplacer(int chunkPosX, int chunkPosY, SerializedApplicationBytes chunkApplicationBytes, int oldChunkPosX, int oldChunkPosY) {
        context.nextFrame(() -> context.getSystemsAdminClient().getMapManager().chunkARemplacer(chunkPosX, chunkPosY, chunkApplicationBytes, oldChunkPosX, oldChunkPosY));
    }

    @Override
    public void deconnectionJoueur(UUID uuid) {
        context.nextFrame(() -> context.getSystemsAdminClient().getPlayerManagerClient().removePlayer(uuid));
    }

    @Override
    public void clientConnexionRemoved() {
        context.nextFrame(() -> {
            context.closeEcs();
            if (context.getScreenType() == ScreenType.GAME_SCREEN) {
                Gdx.app.postRunnable(() -> {
                    context.setScreen(ScreenType.MENU);
                    Popup.popupErreur(context, "Server is close", context.getUiStage());
                });
            } else if (context.getScreenType() == ScreenType.REJOINDRE_MULTI) {
                Popup.popupErreur(context, "Server is close", context.getUiStage());
            }
        });
    }


    public void cellBreak(int worldPosX, int worldPosY) {
        context.nextFrame(() -> {
            int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
            int chunkPosX = MapManager.getChunkPos(worldPosX);
            int chunkPosY = MapManager.getChunkPos(worldPosY);
            int chunkId = context.getSystemsAdminClient().getMapManager().getChunk(chunkPosX, chunkPosY, infChunks);
            int cellId = context.getSystemsAdminClient().getMapManager().getTopCell(chunkId, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
            context.getSystemsAdminClient().getMapManager().damneCell(chunkId, cellId);
        });
    }

    @Override
    public void cellAdd(int worldPosX, int worldPosY, SerializedApplicationBytes cellApplicationBytes) {
        context.nextFrame(() -> {
            int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
            CellArgs cellArgs = context.getSerializerController().getCellSerializerController().decode(cellApplicationBytes);
            int chunkPosX = MapManager.getChunkPos(worldPosX);
            int chunkPosY = MapManager.getChunkPos(worldPosY);
            int chunkId = context.getSystemsAdminClient().getMapManager().getChunk(chunkPosX, chunkPosY, infChunks);
            context.getSystemsAdminClient().getMapManager().newCellInChunk(chunkId, cellArgs);
        });
    }

    @Override
    public void cellSet(int worldPosX, int worldPosY, byte layer, SerializedApplicationBytes cellApplicationBytes) {
        context.nextFrame(() -> {
            CellArgs cellArgs = context.getSerializerController().getCellSerializerController().decode(cellApplicationBytes);
            context.getSystemsAdminClient().getMapManager().setCell(worldPosX, worldPosY, layer, cellArgs);
        });
    }

    @Override
    public void tickBeat(float tickElapseTime, float deltaTime) {
        context.nextFrame(() -> {
            context.getEcsEngine().serverTickBeatMonitoring.addTickElapseTime(tickElapseTime);
            context.getEcsEngine().getTickEmulationInvocationStrategy().addTickBeatDeltaTime(deltaTime);
        });
    }

    @Override
    public void setInventory(UUID inventoryUUID, SerializedApplicationBytes applicationInventoryBytes) {
        context.nextFrame(() -> {
            World world = context.getEcsEngine().getWorld();
            ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
            int inventoryId = context.getSystemsAdminClient().getInventoryManager().getInventoryByUUID(inventoryUUID);
            if (inventoryId == -1) return;
            int[][] inventory = mInventory.get(inventoryId).inventory;
            context.getSystemsAdminClient().getInventoryManager().removeInventory(inventory);
            InventoryArgs inventoryArgs = context.getSerializerController().getInventorySerializerManager().decode(applicationInventoryBytes).apply(context.getSystemsAdminClient().getItemManagerClient());
            int[][] newInventory = inventoryArgs.inventory();
            for (int i = 0; i < inventory.length; i++) {
                System.arraycopy(newInventory[i], 0, inventory[i], 0, newInventory[i].length);
            }
        });
    }

    @Override
    public void setItemOnGroundPos(UUID uuid, int itemRegisterEntryHash, float worldPosX, float worldPosY) {
        context.nextFrame(() -> context.getSystemsAdminClient().getItemManagerClient().setItemOnGroundPos(uuid, itemRegisterEntryHash, worldPosX, worldPosY));
    }

    @Override
    public void supprimeItemOnGround(UUID itemUuid) {
        context.nextFrame(() -> context.getSystemsAdminClient().getItemManagerClient().supprimeItemOnGround(itemUuid));
    }

    @Override
    public void writeOnConsoleMessage(String consoleMessageToWrite) {
        context.nextFrame(() -> context.writeToConsole(consoleMessageToWrite));
    }

    @Override
    public void disconnectMessage(String message) {
        Gdx.app.postRunnable(() -> {
            context.closeEcs();
            context.setScreen(ScreenType.MENU);
            Popup.popupErreur(context, message, context.getUiStage());
        });
    }

    @Override
    public void timeSet(float time) {
        context.nextFrame(() -> context.getSystemsAdminClient().getTimeSystemSimulation().setAccumulatedDelta(time));
    }

    @Override
    public void physicEntity(PhysicEntityArgs physicEntityArgs) {
        context.nextFrame(() -> {
            UUID uuid = physicEntityArgs.uuid();
            float x = physicEntityArgs.x();
            float y = physicEntityArgs.y();
            if (physicEntityArgs.flag() == PhysicEntitySerializerController.ENEMY_FLAG) {
                context.getSystemsAdminClient().getIaManagerClient().otherIa(uuid, x, y);
            }
        });
    }

    @Override
    public void setPlayer(Consumer<Integer> setPlayerConsumer, UUID playerUuid) {
        context.nextFrame(() -> {
            int playerId = context.getEcsEngine().getSystemsAdminClient().getUuidEntityManager().getEntityId(playerUuid);
            if (playerId == -1) return;
            setPlayerConsumer.accept(playerId);
            PositionComponent positionComponent = context.getEcsEngine().getWorld().getMapper(PositionComponent.class).get(playerId);
            context.getSystemsAdminClient().getPlayerManagerClient().setPlayerPos(positionComponent.x, positionComponent.y, playerUuid);
        });
    }

    @Override
    public void playerOutOfRange(UUID playerUuid) {
        context.nextFrame(() -> {
            context.getSystemsAdminClient().getPlayerManagerClient().playerOutOfRange(playerUuid);
        });
    }

    @Override
    public void playerCreateConnexion(UUID playerUuid) {
        context.nextFrame(() -> {
            context.getSystemsAdminClient().getPlayerManagerClient().createPlayerClient(playerUuid);
        });
    }

    @Override
    public void furnaceExtraInfo(UUID furnaceUuid, int lastRemainingTickToBurnFull, int lastTickProcessFull) {
        context.nextFrame(() -> {
            context.getSystemsAdminClient().getFurnaceIconSystem().setFurnaceExtraInfo(furnaceUuid, lastRemainingTickToBurnFull, lastTickProcessFull);
        });
    }

    @Override
    public void energyBatterySetEnergy(UUID energyBatteryUuid, long stored) {
        context.nextFrame(() -> {
            int energyBatteryId = context.getSystemsAdminClient().getUuidEntityManager().getEntityId(energyBatteryUuid);
            context.getSystemsAdminClient().getEnergyBatteryIconSystem().setEnergy(energyBatteryId, stored);
        });
    }

    @Override
    public void energyGeneratorSetInfo(UUID energyGeneratorUuid, int remainingTickToBurn, int lastRemainingTickToBurn) {
        context.nextFrame(() -> {
            int energyGeneratorId = context.getSystemsAdminClient().getUuidEntityManager().getEntityId(energyGeneratorUuid);
            context.getSystemsAdminClient().getEnergyBatteryIconSystem().setEnergyGeneratorInfo(energyGeneratorId, remainingTickToBurn, lastRemainingTickToBurn);
        });
    }

    @Override
    public void playerPickUpItem(UUID playerUuid) {
        context.nextFrame(() -> {
            context.getSoundManager().playItemPickUp();
        });
    }

    @Override
    public void mobDelete(UUID mobUuid) {
        context.nextFrame(() -> {
            int mobId = context.getSystemsAdminClient().getUuidEntityManager().getEntityId(mobUuid);
            context.getSystemsAdminClient().getMobManager().destroyMob(mobId);
        });
    }

    @Override
    public void addParticle(ParticleAddPacket.Particles particle, Vector2 gameCoordinate) {
        context.nextFrame(() -> {
            if (particle == ParticleAddPacket.Particles.HIT) {
                context.getSystemsAdminClient().getParticleEffectsSystem().createHitEffect(gameCoordinate);
            }
        });
    }

    @Override
    public void mobAttackCoolDown(UUID mobUuid, int cooldown) {
        context.nextFrame(() -> {
            context.getSystemsAdminClient().getMobManagerClient().mobAttackCoolDown(mobUuid, cooldown);
        });
    }
}
