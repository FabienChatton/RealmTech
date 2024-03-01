package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.level.cell.CellManager;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.packet.clientPacket.CellBreakPacket;
import ch.realmtech.server.packet.clientPacket.ChunkADamnePacket;
import ch.realmtech.server.packet.clientPacket.ChunkAMonterPacket;
import ch.realmtech.server.registery.CellRegisterEntry;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.serialize.cell.CellArgs;
import ch.realmtech.server.serialize.exception.IllegalMagicNumbers;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.utils.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.util.*;
import java.util.zip.ZipException;

@All(PlayerConnexionComponent.class)
public class MapSystemServer extends IteratingSystem implements CellManager {
    private final static Logger logger = LoggerFactory.getLogger(MapSystemServer.class);
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private SystemsAdminServer systemsAdminServer;

    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    private InfMapComponent infMapComponent;
    private SaveMetadataComponent infMetaDonnesComponent;
    int renderDistance;
    private Map<Integer, List<Position>> chunkADamner;
    private Map<Integer, List<Position>> chunkAObtenir;
    private Map<Integer, List<Position>> chunkAGarders;
    private boolean isInit = false;
    public void initMap() {
        infMapComponent = mInfMap.get(systemsAdminServer.tagManager.getEntityId("infMap"));
        infMetaDonnesComponent = infMapComponent.getMetaDonnesComponent(world);

        chunkADamner = new HashMap<>();
        chunkAObtenir = new HashMap<>();
        chunkAGarders = new HashMap<>();
        isInit = true;
    }

    @Override
    protected void begin() {
        renderDistance = serverContext.getOptionServer().renderDistance.get();
    }

    @Override
    protected void process(int playerId) {
        PositionComponent positionPlayerComponent = mPosition.get(playerId);
        if (positionPlayerComponent == null) return;
        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        int chunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(positionPlayerComponent.x));
        int chunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(positionPlayerComponent.y));
        if (playerConnexionComponent.ancienChunkPos == null || !(playerConnexionComponent.ancienChunkPos[0] == chunkPosX && playerConnexionComponent.ancienChunkPos[1] == chunkPosY)) {
            List<Position> chunkADamnerPos = trouveChunkADamner(playerConnexionComponent.chunkPoss, chunkPosX, chunkPosY, renderDistance);
            List<Position> chunkAObtenirPos = trouveChunkAObtenir(playerConnexionComponent.chunkPoss, chunkPosX, chunkPosY);
            List<Position> chunkAGarder = trouveChunkAGarder(chunkPosX, chunkPosY);

            chunkADamner.put(playerId, chunkADamnerPos);
            chunkAObtenir.put(playerId, chunkAObtenirPos);
            chunkAGarders.put(playerId, chunkAGarder);
                if (playerConnexionComponent.ancienChunkPos == null) {
                    playerConnexionComponent.ancienChunkPos = new int[2];
                }
            playerConnexionComponent.ancienChunkPos[0] = chunkPosX;
            playerConnexionComponent.ancienChunkPos[1] = chunkPosY;
        }
    }

    @Override
    protected void end() {
        if (isInit && chunkADamner.isEmpty() && chunkAObtenir.isEmpty() || chunkAGarders == null) return;

        List<Position> tousChunkAGarder = new ArrayList<>();
        for (List<Position> chunkAGarder : chunkAGarders.values()) {
            tousChunkAGarder.addAll(chunkAGarder);
        }

        List<Position> tousChunkADamner = new ArrayList<>();
        for (List<Position> chunkADamnerPoss : chunkADamner.values()) {
            tousChunkADamner.addAll(chunkADamnerPoss);
        }

        List<Position> tousChunkADamnerSansGarder = new ArrayList<>(tousChunkADamner);
        tousChunkADamnerSansGarder.removeIf(tousChunkAGarder::contains);


        List<Position> tousChunkAObtenir = new ArrayList<>();
        for (List<Position> chunkAObtenirPoss : chunkAObtenir.values()) {
            tousChunkAObtenir.addAll(chunkAObtenirPoss);
        }

        int[] oldChunks = new int[tousChunkADamnerSansGarder.size()];
        for (int i = 0; i < tousChunkADamnerSansGarder.size(); i++) {
            Position chunkADamnerPoss = tousChunkADamnerSansGarder.get(i);
            int chunkId = systemsAdminServer.mapManager.getChunk(chunkADamnerPoss.x(), chunkADamnerPoss.y(), infMapComponent.infChunks);
            oldChunks[i] = chunkId;
            damneChunkServerDirty(chunkId, infMetaDonnesComponent);
        }

        int[] newChunks = new int[tousChunkAObtenir.size()];
        for (int i = 0; i < tousChunkAObtenir.size(); i++) {
            Position chunkAObtenirPoss = tousChunkAObtenir.get(i);
            newChunks[i] = getCacheOrGenerateChunk(infMapComponent, infMetaDonnesComponent, chunkAObtenirPoss.x(), chunkAObtenirPoss.y());
        }

        infMapComponent.infChunks = systemsAdminServer.mapManager.ajouterChunkAMap(infMapComponent.infChunks, newChunks, oldChunks);


        this.chunkADamner.forEach((playerId, chunkPoss) -> {
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
            chunkPoss.forEach(chunkPos -> {
                serverContext.getServerConnexion().sendPacketTo(new ChunkADamnePacket(chunkPos.x(), chunkPos.y()), playerConnexionComponent.channel);
                playerConnexionComponent.chunkPoss.remove(chunkPos);
            });
        });

        this.chunkAObtenir.forEach((playerId, chunkPoss) -> {
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
            chunkPoss.forEach(chunkPos -> {
                int chunkId = systemsAdminServer.mapManager.getChunk(chunkPos.x(), chunkPos.y(), infMapComponent.infChunks);
                InfChunkComponent infChunkComponent = mChunk.get(chunkId);
                serverContext.getServerConnexion().sendPacketTo(new ChunkAMonterPacket(serverContext.getSerializerController().getChunkSerializerController().encode(infChunkComponent)), playerConnexionComponent.channel);
                playerConnexionComponent.chunkPoss.add(new Position(chunkPos.x(), chunkPos.y()));
            });
        });

        chunkADamner.clear();
        chunkAObtenir.clear();
    }

    private boolean chunkSansChangement(List<Position> chunkPoss, int i, int j) {
        boolean trouve = false;
        for (Position position : chunkPoss) {
            if (position.x() == i && position.y() == j) {
                trouve = true;
                break;
            }
        }
        return !trouve;
    }

    public List<Position> trouveChunkADamner(List<Position> poss, int chunkPosX, int chunkPosY, int renderDistance) {
        List<Position> ret = new ArrayList<>(2 * renderDistance + 1);
        for (Position position : poss) {
            if (!chunkEstDansLaRenderDistance(position, chunkPosX, chunkPosY, renderDistance)) {
                ret.add(position);
            }
        }
        return ret;
    }

    public List<Position> trouveChunkAObtenir(List<Position> poss, int chunkPosX, int chunkPosY) {
        List<Position> ret = trouveChunkAGarder(chunkPosX, chunkPosY);
        for (Position position : poss) {
            ret.remove(position);
        }
        return ret;
    }

    public List<Position> trouveChunkAGarder(int chunkPosX, int chunkPosY) {
        List<Position> ret = new ArrayList<>(2 * renderDistance + 1);
        for (int i = -renderDistance + chunkPosX; i <= renderDistance + chunkPosX; i++) {
            for (int j = -renderDistance + chunkPosY; j <= renderDistance + chunkPosY; j++) {
                ret.add(new Position(i, j));
            }
        }
        return ret;
    }


    public void damneChunkServerDirty(int chunkId, SaveMetadataComponent saveMetadataComponent) {
        try {
            systemsAdminServer.saveInfManager.saveInfChunk(chunkId, SaveInfManager.getSavePath(saveMetadataComponent.saveName));
            systemsAdminServer.mapManager.supprimeChunk(chunkId);
        } catch (IOException e) {
            InfChunkComponent infChunkComponent = mChunk.get(chunkId);
            logger.error("Chunk {},{} has not been saved: {}", infChunkComponent.chunkPosX, infChunkComponent.chunkPosY, e.getMessage());
        }
    }

    public int[] damneChunkServer(int[] infChunks, int chunkId, SaveMetadataComponent saveMetadataComponent) {
        try {
            systemsAdminServer.saveInfManager.saveInfChunk(chunkId, SaveInfManager.getSavePath(saveMetadataComponent.saveName));
            systemsAdminServer.mapManager.supprimeChunk(chunkId);
        } catch (IOException e) {
            InfChunkComponent infChunkComponent = mChunk.get(chunkId);
            logger.error("Le chunk {},{} n'a pas été sauvegardé correctement", infChunkComponent.chunkPosX, infChunkComponent.chunkPosY);
        }
        return systemsAdminServer.mapManager.supprimerChunkAMap(infChunks, chunkId);
    }

    public boolean chunkEstDansLaRenderDistance(Position position, int posX, int posY, int renderDistance) {
        int dstX = Math.abs(posX - position.x());
        int dstY = Math.abs(posY - position.y());
        return dstX <= renderDistance && dstY <= renderDistance;
    }

    private int getCacheOrGenerateChunk(InfMapComponent infMapComponent, SaveMetadataComponent saveMetadataComponent, int chunkX, int chunkY) {
        // regarde si le chunk est déjà present dans la map
        int chunkId = systemsAdminServer.mapManager.getChunk(chunkX, chunkY, infMapComponent.infChunks);
        if (chunkId != -1) {
            return chunkId;
        }

        try {
            chunkId = systemsAdminServer.saveInfManager.readSavedInfChunk(chunkX, chunkY, saveMetadataComponent.saveName);
        } catch (FileNotFoundException | BufferUnderflowException | IllegalMagicNumbers | ZipException | EOFException e) {
            if (e instanceof BufferUnderflowException || e instanceof EOFException) logger.error("The chunk {},{} was corrupted", chunkX, chunkY);
            if (e instanceof IllegalMagicNumbers) logger.error("The chunk {},{} was not recognise has a chunk file. Maybe the chunk version is < 9", chunkX, chunkY);
            if (e instanceof ZipException) logger.error("The chunk {},{} was not compressed", chunkX, chunkY);
            logger.info("Generating the chunk {},{}", chunkX, chunkY);

            chunkId = systemsAdminServer.mapManager.generateNewChunk(saveMetadataComponent, chunkX, chunkY);
            try {
                systemsAdminServer.saveInfManager.saveInfChunk(chunkId, SaveInfManager.getSavePath(saveMetadataComponent.saveName));
            } catch (IOException ex) {
                logger.error(e.getMessage(), ex);
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return chunkId;
    }

    @Override
    public void breakCell(int worldPosX, int worldPosY, @Null ItemRegisterEntry itemDropRegisterEntry, int playerSrc) {
        InfMapComponent infMapComponent = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class);
        int chunk = systemsAdminServer.mapManager.getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infMapComponent.infChunks);
        int topCell = systemsAdminServer.mapManager.getTopCell(chunk, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
        if (topCell == -1) return;
        systemsAdminServer.mapManager.damneCell(chunk, topCell);
        if (itemDropRegisterEntry != null) {
            systemsAdminServer.itemManagerServer.newItemOnGround(worldPosX, worldPosY, itemDropRegisterEntry, UUID.randomUUID());
        }
        serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new CellBreakPacket(worldPosX, worldPosY), MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY));
    }

    public int placeItemToCell(UUID itemToPlaceUuid, int worldPosX, int worldPosY) {
        int itemId = systemsAdminServer.uuidEntityManager.getEntityId(itemToPlaceUuid);
        if (!mItem.has(itemId)) return -1;
        ItemComponent itemComponent = mItem.get(itemId);
        CellRegisterEntry placeCell = itemComponent.itemRegisterEntry.getItemBehavior().getPlaceCell();
        if (placeCell == null) return -1;

        int chunkPosX = MapManager.getChunkPos(worldPosX);
        int chunkPosY = MapManager.getChunkPos(worldPosY);

        InfMapComponent infMapComponent = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class);
        int chunkId = systemsAdminServer.mapManager.getChunk(chunkPosX, chunkPosY, infMapComponent.infChunks);

        // can only place on item per layer
        if (systemsAdminServer.mapManager.getCell(chunkId, worldPosX, worldPosY, placeCell.getCellBehavior().getLayer()) != -1) {
            return -1;
        }

        int topCellId = systemsAdminServer.mapManager.getTopCell(chunkId, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));
        CellComponent topCellComponent = mCell.get(topCellId);
        // can not place cell on top
        if (!topCellComponent.cellRegisterEntry.getCellBehavior().isCanPlaceCellOnTop()) {
            return -1;
        }

        byte innerChunkX = MapManager.getInnerChunk(worldPosX);
        byte innerChunkY = MapManager.getInnerChunk(worldPosY);
        mItem.remove(itemId);
        return systemsAdminServer.mapManager.newCellInChunk(chunkId, new CellArgs(placeCell, Cells.getInnerChunkPos(innerChunkX, innerChunkY)));
    }


    public void deleteChestDropItemServer(int motherId) {
        InventoryComponent chestInventory = systemsAdminServer.inventoryManager.getChestInventory(motherId);
        CellComponent cellComponent = mCell.get(motherId);
        InfChunkComponent infChunkComponent = mChunk.get(cellComponent.chunkId);
        int worldPosX = MapManager.getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX());
        int worldPosY = MapManager.getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY());
        for (int i = 0; i < chestInventory.inventory.length; i++) {
            for (int j = 0; j < InventoryManager.tailleStack(chestInventory.inventory[i]); j++) {
                systemsAdminServer.itemManagerServer.dropItem(chestInventory.inventory[i][j], worldPosX, worldPosY);
            }
        }
    }
}
