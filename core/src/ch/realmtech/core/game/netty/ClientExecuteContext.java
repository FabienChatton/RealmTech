package ch.realmtech.core.game.netty;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.system.PlayerManagerClient;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.core.screen.ScreenType;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.divers.Notify;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.mod.quests.QuestManagerEntry;
import ch.realmtech.server.packet.clientPacket.ClientExecute;
import ch.realmtech.server.packet.clientPacket.ConnexionJoueurReussitPacket;
import ch.realmtech.server.packet.clientPacket.ParticleAddPacket;
import ch.realmtech.server.quests.QuestPlayerProperty;
import ch.realmtech.server.registry.MobEntry;
import ch.realmtech.server.registry.QuestEntry;
import ch.realmtech.server.registry.RegistryUtils;
import ch.realmtech.server.serialize.cell.CellArgs;
import ch.realmtech.server.serialize.inventory.InventoryArgs;
import ch.realmtech.server.serialize.physicEntity.EnemyArgs;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        context.nextFrame(() -> {
            context.getSystemsAdminClient().getMapManager().chunkAMounter(applicationChunkBytes);
            context.getSystemsAdminClient().getMapRendererSystem().refreshCache();
        });
    }

    @Override
    public void chunkADamner(int chunkPosX, int chunkPosY) {
        context.nextFrame(() -> {
            context.getSystemsAdminClient().getMapManager().damneChunkClient(chunkPosX, chunkPosY);
            context.getSystemsAdminClient().getMapRendererSystem().refreshCache();
        });
    }

    @Override
    public void chunkARemplacer(int chunkPosX, int chunkPosY, SerializedApplicationBytes chunkApplicationBytes, int oldChunkPosX, int oldChunkPosY) {
        context.nextFrame(() -> {
            context.getSystemsAdminClient().getMapManager().chunkARemplacer(chunkPosX, chunkPosY, chunkApplicationBytes, oldChunkPosX, oldChunkPosY);
            context.getSystemsAdminClient().getMapRendererSystem().refreshCache();
        });
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
            context.getSystemsAdminClient().getMapRendererSystem().refreshCache();
        });
    }

    @Override
    public void cellSet(int worldPosX, int worldPosY, byte layer, SerializedApplicationBytes cellApplicationBytes) {
        context.nextFrame(() -> {
            CellArgs cellArgs = context.getSerializerController().getCellSerializerController().decode(cellApplicationBytes);
            context.getSystemsAdminClient().getMapManager().setCell(worldPosX, worldPosY, layer, cellArgs);
            context.getSystemsAdminClient().getMapRendererSystem().refreshCache();
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
    public void enemySet(EnemyArgs enemyArgs) {
        context.nextFrame(() -> {
            UUID uuid = enemyArgs.uuid();
            float x = enemyArgs.x();
            float y = enemyArgs.y();
            MobEntry mobEntry = RegistryUtils.findEntryUnsafe(context.getRootRegistry(), enemyArgs.enemyEntryId());
            context.getSystemsAdminClient().getEnemyManagerClient().setMob(uuid, x, y, mobEntry);
        });
    }

    @Override
    public void enemyDelete(UUID mobUuid) {
        context.nextFrame(() -> {
            context.getSystemsAdminClient().getEnemyManagerClient().deleteMob(mobUuid);
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
            if (!context.getSystemsAdminClient().getTagManager().isRegistered(PlayerManagerClient.MAIN_PLAYER_TAG)) {
                logger.warn("Creating other player but the main player is not registered. This can be lead to a other player to be the main player");
            }
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
    public void addParticle(ParticleAddPacket.Particles particle, Vector2 gameCoordinate) {
        context.nextFrame(() -> {
            if (particle == ParticleAddPacket.Particles.HIT) {
                context.getSystemsAdminClient().getParticleEffectsSystem().createHitEffect(gameCoordinate);
            }
        });
    }

    @Override
    public void enemyAttackCoolDown(UUID mobUuid, int cooldown) {
        context.nextFrame(() -> {
            context.getSystemsAdminClient().getMobManagerClient().mobAttackCoolDown(mobUuid, cooldown);
        });
    }

    @Override
    public void nextFrame(Runnable runnable) {
        context.nextFrame(runnable);
    }

    @Override
    public void questSetCompleted(int questEntryId, long completedTimestamp) {
        context.nextFrame(() -> {
            int mainPlayer = context.getSystemsAdminClient().getPlayerManagerClient().getMainPlayer();
            QuestPlayerPropertyComponent questPlayerPropertyComponent = context.getEcsEngine().getWorld().getMapper(QuestPlayerPropertyComponent.class).get(mainPlayer);
            QuestEntry questEntryCompleted = RegistryUtils.findEntryUnsafe(context.getRootRegistry(), questEntryId);
            QuestPlayerProperty questPlayerPropertyToCompleted = questPlayerPropertyComponent.getQuestPlayerProperties().stream().filter((questPlayerProperty) -> questPlayerProperty.getQuestEntry() == questEntryCompleted).findFirst().orElseThrow();
            questPlayerPropertyToCompleted.setCompleted(completedTimestamp);
        });
    }

    @Override
    public void playerCreateQuest(SerializedApplicationBytes questSerializedApplicationBytes) {
        context.nextFrame(() -> {
            List<QuestPlayerProperty> completedQuestPlayerProperties = context.getSerializerController().getQuestSerializerController().decode(questSerializedApplicationBytes);
            QuestManagerEntry questManagerEntry = RegistryUtils.findEntryOrThrow(context.getSystemsAdminClient().getRootRegistry(), QuestManagerEntry.class);
            List<QuestPlayerProperty> questPlayerProperties = questManagerEntry.mapToQuestEntry(completedQuestPlayerProperties);

            int playerId = context.getPlayerId();
            context.getEcsEngine().getWorld().getMapper(QuestPlayerPropertyComponent.class).create(playerId).set(questPlayerProperties);
        });
    }

    @Override
    public void notifySend(SerializedApplicationBytes notifySerializedApplicationBytes) {
        context.nextFrame(() -> {
            Notify notify = context.getSerializerController().getNotifySerializerController().decode(notifySerializedApplicationBytes);
            context.addNotify(notify);
        });
    }
}
