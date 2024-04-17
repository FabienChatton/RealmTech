package ch.realmtech.server.netty;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.level.cell.BreakCell;
import ch.realmtech.server.mod.options.server.VerifyTokenOptionEntry;
import ch.realmtech.server.packet.clientPacket.*;
import ch.realmtech.server.packet.serverPacket.ServerExecute;
import ch.realmtech.server.registry.ItemEntry;
import ch.realmtech.server.registry.RegistryUtils;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ServerExecuteContext implements ServerExecute {
    private final static Logger logger = LoggerFactory.getLogger(ServerExecuteContext.class);
    private final ServerContext serverContext;
    private final VerifyTokenOptionEntry verifyToken;

    public ServerExecuteContext(ServerContext serverContext) {
        this.serverContext = serverContext;
        verifyToken = RegistryUtils.findEntryOrThrow(serverContext.getRootRegistry(), VerifyTokenOptionEntry.class);
    }

    @Override
    public ServerContext getContext() {
        return serverContext;
    }

    @Override
    public void connexionPlayerRequest(Channel clientChanel, String username) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            logger.info("Player {} try to login. {}", username, clientChanel);
            UUID playerUuid;
            try {
                if (serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByUsername(username) != -1)
                    throw new IllegalArgumentException("A Player with this username already existe on the server");
                playerUuid = UUID.fromString(serverContext.getAuthController().verifyAccessToken(username));
                logger.info("Player {} [{}] has successfully been authenticated. {}. Verify access token: {}", username, playerUuid, clientChanel, verifyToken.getValue());
                if (serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByUuid(playerUuid) != -1) {
                    throw new IllegalArgumentException("A player with the same uuid already existe on the server");
                }
            } catch (Exception e) {
                serverContext.getServerConnexion().sendPacketTo(new DisconnectMessagePacket(e.getMessage()), clientChanel);
                logger.info("Player {} has failed to been authenticated. Cause : {}, {}", username, e.getMessage(), clientChanel);
                if (clientChanel != null) {
                    serverContext.getEcsEngineServer().nextTick(clientChanel::close);
                }
                return;
            }
            try {
                ConnexionJoueurReussitPacket.ConnexionJoueurReussitArg connexionJoueurReussitArg = serverContext.getSystemsAdminServer().getPlayerManagerServer().createPlayerServer(clientChanel, playerUuid, username);
                serverContext.getServerConnexion().sendPacketTo(new ConnexionJoueurReussitPacket(connexionJoueurReussitArg), clientChanel);
                serverContext.getSystemsAdminServer().getPlayerManagerServer().setPlayerUsername(playerUuid, username);
            } catch (Exception e) {
                serverContext.getServerConnexion().sendPacketTo(new DisconnectMessagePacket(e.getMessage()), clientChanel);
                serverContext.getSystemsAdminServer().getPlayerManagerServer().removePlayer(clientChanel);
                logger.info("Player {} has failed to been created. Cause : {}, {}", username, e.getMessage(), clientChanel);
                if (clientChanel != null) {
                    serverContext.getEcsEngineServer().nextTick(clientChanel::close);
                }
                return;
            }

            // new player with new connexion get all players.
            // players already on server get this player.
            IntBag players = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayers();
            int[] playersData = players.getData();
            int thisPlayerId = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByChannel(clientChanel);
            for (int i = 0; i < players.size(); i++) {
                int playerId = playersData[i];
                if (thisPlayerId == playerId) continue;
                UUID uuid = serverContext.getSystemsAdminServer().getUuidEntityManager().getEntityUuid(playerId);
                serverContext.getServerConnexion().sendPacketTo(new PlayerCreateConnexion(uuid), clientChanel);
            }
            serverContext.getServerConnexion().broadCastPacketExcept(new PlayerCreateConnexion(playerUuid), clientChanel);
        });
    }

    @Override
    public void removePlayer(Channel channel) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            int playerId = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByChannel(channel);
            if (playerId == -1) return;
            try {
                serverContext.getSystemsAdminServer().getPlayerManagerServer().savePlayer(playerId);
            } catch (IOException e) {
                String username = serverContext.getEcsEngineServer().getWorld().getMapper(PlayerConnexionComponent.class).get(playerId).getUsername();
                logger.warn("Can not save player inventory of {}. : {} ", username, e.getMessage());
            }
            serverContext.getSystemsAdminServer().getPlayerManagerServer().removePlayer(channel);
            UUID playerUuid = serverContext.getSystemsAdminServer().getUuidEntityManager().getEntityUuid(playerId);
            serverContext.getServerConnexion().broadCastPacketExcept(new DeconnectionJoueurPacket(playerUuid), channel);
        });
    }

    @Override
    public void playerMove(Channel clientChannel, byte inputKeys) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            serverContext.getSystemsAdminServer().getPlayerMouvementSystemServer().playerMove(clientChannel, inputKeys);
        });
    }

    @Override
    public void cellBreakRequest(Channel clientChannel, int worldPosX, int worldPosY, UUID itemUsedUuid) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            int playerId = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByChannel(clientChannel);
            PlayerConnexionComponent playerConnexionComponent = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerConnexionComponentByChannel(clientChannel);

            InventoryComponent playerChestInventory = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(playerId);
            int itemUsedId = serverContext.getSystemsAdminServer().getInventoryManager().getItemInInventoryByUuid(playerChestInventory, itemUsedUuid);
            ItemEntry itemUsed;
            if (itemUsedId == -1) {
                itemUsed = null;
            } else {
                itemUsed = serverContext.getEcsEngineServer().getWorld().getMapper(ItemComponent.class).get(itemUsedId).itemRegisterEntry;
            }
            InfMapComponent infMapComponent = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class);
            int[] infChunks = infMapComponent.infChunks;
            int chunkId = serverContext.getSystemsAdminServer().getMapManager().getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infChunks);
            int cellId = serverContext.getSystemsAdminServer().getMapManager().getTopCell(chunkId, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
            ComponentMapper<CellComponent> mCell = serverContext.getEcsEngineServer().getWorld().getMapper(CellComponent.class);
            CellComponent cellComponent = mCell.get(cellId);
            BreakCell breakCellEvent = cellComponent.cellRegisterEntry.getCellBehavior().getBreakCellEvent();
            if (breakCellEvent != null) {
                breakCellEvent.breakCell(serverContext.getSystemsAdminServer().getMapSystemServer(), serverContext.getEcsEngineServer().getWorld(), chunkId, cellId, itemUsed, playerId);
            }
        });
    }

    @Override
    public void getPlayerInventorySession(Channel clientChannel) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            int playerId = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByChannel(clientChannel);
            UUID chestInventoryUuid = serverContext.getSystemsAdminServer().getUuidEntityManager().getEntityUuid(serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(playerId));
            getInventory(clientChannel, chestInventoryUuid);
        });
    }

    @Override
    public void consoleCommande(Channel clientChannel, String stringCommande) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (PrintWriter printWriter = new PrintWriter(baos, true, StandardCharsets.US_ASCII)) {
                serverContext.getCommandeExecute().execute(stringCommande, printWriter);
            }
            serverContext.getServerConnexion().sendPacketTo(new WriteToConsolePacket(baos.toString()), clientChannel);
        });
    }

    @Override
    public void moveStackToStackNumberRequest(Channel clientChannel, UUID srcInventory, UUID dstInventory, UUID[] itemsToMove, int slotIndex) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            try {
                int[] mutatedInventories = serverContext.getSystemsAdminServer().getInventoryManager().moveStackToStackRequest(srcInventory, dstInventory, itemsToMove, slotIndex);
                ComponentMapper<InventoryComponent> mInventory = serverContext.getEcsEngineServer().getWorld().getMapper(InventoryComponent.class);
                InventoryComponent srcInventoryComponent = mInventory.get(serverContext.getSystemsAdminServer().getInventoryManager().getInventoryByUUID(srcInventory));
                InventoryComponent dstInventoryComponent = mInventory.get(serverContext.getSystemsAdminServer().getInventoryManager().getInventoryByUUID(dstInventory));
                serverContext.getServerConnexion().sendPacketTo(new InventorySetPacket(srcInventory, serverContext.getSerializerController().getInventorySerializerManager().encode(srcInventoryComponent)), clientChannel);
                serverContext.getServerConnexion().sendPacketTo(new InventorySetPacket(dstInventory, serverContext.getSerializerController().getInventorySerializerManager().encode(dstInventoryComponent)), clientChannel);
                if (mutatedInventories != null) {
                    for (int mutatedInventory : mutatedInventories) {
                        UUID mutatedInventoryUuid = serverContext.getSystemsAdminServer().getUuidEntityManager().getEntityUuid(mutatedInventory);
                        serverContext.getServerConnexion().sendPacketTo(new InventorySetPacket(mutatedInventoryUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(mInventory.get(mutatedInventory))), clientChannel);
                    }
                }
            } catch (IllegalAccessError e) {
                logger.warn(e.getMessage());
            }
        });
    }

    @Override
    public void getInventory(Channel clientChannel, UUID inventoryUuid) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            InventoryComponent inventoryComponent = serverContext.getSystemsAdminServer().getInventoryManager().getInventoryComponentByUUID(inventoryUuid);
            if (inventoryComponent == null) return;
            SerializedApplicationBytes applicationInventoryBytes = serverContext.getSerializerController().getInventorySerializerManager().encode(inventoryComponent);
            serverContext.getServerConnexion().sendPacketTo(new InventorySetPacket(inventoryUuid, applicationInventoryBytes), clientChannel);
        });
    }

    @Override
    public void itemToCellPlace(Channel clientChannel, UUID itemToPlaceUuid, int worldPosX, int worldPosY) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            int cellPlaced = serverContext.getSystemsAdminServer().getMapSystemServer().placeItemToCell(itemToPlaceUuid, worldPosX, worldPosY);
            if (cellPlaced != -1) {
                int playerId = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByChannel(clientChannel);
                int chestInventoryId = serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(playerId);
                int itemId = serverContext.getSystemsAdminServer().getUuidEntityManager().getEntityId(itemToPlaceUuid);
                serverContext.getSystemsAdminServer().getInventoryManager().removeItemInInventory(chestInventoryId, itemId);
                SerializedApplicationBytes cellApplicationBytes = serverContext.getSerializerController().getCellSerializerController().encode(cellPlaced);
                PositionComponent playerPositionComponent = serverContext.getEcsEngineServer().getWorld().getMapper(PositionComponent.class).get(playerId);
                int chunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(playerPositionComponent.x));
                int chunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(playerPositionComponent.y));
                serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new CellAddPacket(worldPosX, worldPosY, cellApplicationBytes), chunkPosX, chunkPosY);

                UUID inventoryUuid = serverContext.getSystemsAdminServer().getUuidEntityManager().getEntityUuid(chestInventoryId);
                ComponentMapper<InventoryComponent> mInventory = serverContext.getEcsEngineServer().getWorld().getMapper(InventoryComponent.class);
                serverContext.getServerConnexion().sendPacketTo(new InventorySetPacket(inventoryUuid, serverContext.getSerializerController().getInventorySerializerManager().encode(mInventory.get(chestInventoryId))), clientChannel);
            }
        });
    }

    @Override
    public void getTime(Channel clientChannel) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            float time = serverContext.getSystemsAdminServer().getTimeSystem().getAccumulatedDelta();
            serverContext.getServerConnexion().sendPacketTo(new TimeSetPacket(time), clientChannel);
        });
    }

    @Override
    public void rotateFaceCellRequest(Channel clientChannel, int worldPosX, int worldPosY, byte layer, byte faceToRotate) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            InfMapComponent infMapComponent = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class);
            int chunkId = serverContext.getSystemsAdminServer().getMapManager().getChunkByWorldPos(worldPosX, worldPosY, infMapComponent.infChunks);
            if (chunkId == -1) {
                logger.warn("chunk not found for face rotate. worldPosX: {}, worldPosY: {}", worldPosX, worldPosY);
                return;
            }
            int cellId = serverContext.getSystemsAdminServer().getMapManager().getCell(chunkId, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY), layer);
            if (cellId == -1) {
                logger.warn("cell not found for face rotate. worldPosX: {}, worldPosY: {}, layer: {}", worldPosX, worldPosY, layer);
            }
            serverContext.getSystemsAdminServer().getMapManager().rotateCellFace(cellId, faceToRotate);

            serverContext.getSystemsAdminServer().getDirtyCellSystem().addDirtyCell(cellId);
        });
    }

    @Override
    public void subscribeToEntity(Channel clientChannel, UUID entityUuid) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            int playerId = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByChannel(clientChannel);
            serverContext.getSystemsAdminServer().getPlayerSubscriptionSystem().addEntitySubscriptionToPlayer(playerId, entityUuid);
        });
    }

    @Override
    public void unSubscribeToEntity(Channel clientChannel, UUID entityUuid) {
        serverContext.getEcsEngineServer().nextTick(() -> {
            int playerId = serverContext.getSystemsAdminServer().getPlayerManagerServer().getPlayerByChannel(clientChannel);
            serverContext.getSystemsAdminServer().getPlayerSubscriptionSystem().removeEntitySubscriptionToPlayer(playerId, entityUuid);
        });
    }

    @Override
    public void playerWeaponShot(Channel clientChannel, Vector2 vectorClick) {
        serverContext.getEcsEngineServer().nextTick(() -> serverContext.getSystemsAdminServer().getWeaponRayManager().playerWeaponShot(clientChannel, vectorClick));
    }
}
