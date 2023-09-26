package ch.realmtechServer.ecs.system;

import ch.realmtechServer.PhysiqueWorldHelper;
import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.level.cell.CreatePhysiqueBody;
import ch.realmtechServer.level.map.WorldMap;
import ch.realmtechServer.level.worldGeneration.PerlinNoise;
import ch.realmtechServer.options.DataCtrl;
import ch.realmtechServer.registery.CellRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.*;

public class MapManager extends Manager {
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private DataCtrl dataCtrl;
    @Wire
    private BodyDef bodyDef;
    @Wire
    private FixtureDef fixtureDef;
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;

    public static int getWorldPos(int chunkPos, int innerChunk) {
        return chunkPos * WorldMap.CHUNK_SIZE + innerChunk;
    }

    /**
     * Récupère une position dans un chunk via la position du monde.
     *
     * @param world La position dans le monde.
     * @return La position dans le chunk.
     */
    public static byte getInnerChunk(int world) {
        if (world < 0) {
            return (byte) ((byte) (world % WorldMap.CHUNK_SIZE + WorldMap.CHUNK_SIZE) - 1);
        } else {
            return (byte) (world % WorldMap.CHUNK_SIZE);
        }
    }

    /**
     * @param index l'index d'un tableau
     * @return la position x dans le chunk
     */
    public static byte getInnerChunkX(short index) {
        return (byte) Math.abs(index % WorldMap.CHUNK_SIZE);
    }

    public static byte getInnerChunkY(short index) {
        return (byte) Math.abs(index / WorldMap.CHUNK_SIZE);
    }

    public static byte getInnerChunk(float gameCoordinate) {
        if (gameCoordinate > -1 && gameCoordinate < 0) {
            return 15;
        } else {
            return getInnerChunk((int) gameCoordinate);
        }
    }

    public static int getChunkPos(int worldPos) {
        return (worldPos < 0 ? worldPos - WorldMap.CHUNK_SIZE : worldPos) / WorldMap.CHUNK_SIZE;
    }

    public static int getChunkPos(float gameCoordinate) {
        if (gameCoordinate > -1 && gameCoordinate < 0) {
            return -1;
        } else {
            return getChunkPos((int) gameCoordinate);
        }
    }

    public int getChunk(int chunkPosX, int chunkPosY, int[] infChunks) {
        for (int infChunk : infChunks) {
            InfChunkComponent infChunkComponent = mChunk.get(infChunk);
            if (chunkPosX == infChunkComponent.chunkPosX && chunkPosY == infChunkComponent.chunkPosY) {
                return infChunk;
            }
        }
        throw new NoSuchElementException("Il n'existe pas de chunk à la position " + chunkPosX + "," + chunkPosY + " dans cette map");
    }

    public void damneCell(int chunkId, int cellId) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int indexCell = -1;
        int[] cellIds = infChunkComponent.infCellsId;
        for (int i = 0; i < cellIds.length; i++) {
            if (cellId == cellIds[i]) {
                indexCell = i;
                break;
            }
        }
        int[] newCellIds = new int[cellIds.length - 1];
        System.arraycopy(cellIds, 0, newCellIds, 0, indexCell);
        System.arraycopy(cellIds, indexCell + 1, newCellIds, indexCell, newCellIds.length - indexCell);
        infChunkComponent.infCellsId = newCellIds;
        supprimeCell(cellId);
    }

    public int generateNewChunk(int mapId, int chunkPosX, int chunkPosY) {
        int chunkId = world.create();
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        List<Integer> cellsId = new ArrayList<>(WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE);
        for (short i = 0; i < WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE; i++) {
            int[] cells = generateNewCells(infMapComponent.infMetaDonnees, chunkId, chunkPosX, chunkPosY, i);
            for (int j = 0; j < cells.length; j++) {
                cellsId.add(cells[j]);
            }
        }
        InfChunkComponent infChunkComponent = world.edit(chunkId).create(InfChunkComponent.class);
        infChunkComponent.set(chunkPosX, chunkPosY, cellsId.stream().mapToInt(x -> x).toArray());
        return chunkId;
    }
    private int[] generateNewCells(int metaDonnees, int chunkId, int chunkPosX, int chunkPosY, short index) {
        byte innerChunkX = MapManager.getInnerChunkX(index);
        byte innerChunkY = MapManager.getInnerChunkY(index);
        int worldX = MapManager.getWorldPos(chunkPosX, innerChunkX);
        int worldY = MapManager.getWorldPos(chunkPosY, innerChunkY);
        PerlinNoise perlinNoise = mMetaDonnees.get(metaDonnees).perlinNoise;
        final CellRegisterEntry[] cellRegisterEntries = perlinNoise.generateCell(worldX, worldY);
        final int[] cellIds = new int[(int) Arrays.stream(cellRegisterEntries).filter(Objects::nonNull).count()];
        for (int i = 0; i < cellRegisterEntries.length; i++) {
            CellRegisterEntry cellRegisterEntry = cellRegisterEntries[i];
            if (cellRegisterEntry != null) {
                cellIds[i] = world.getSystem(MapManager.class).newCell(chunkId, chunkPosX, chunkPosY, innerChunkX, innerChunkY, cellRegisterEntry);
            }
        }
        return cellIds;
    }

    public int newCell(int chunkId, int chunkPosX, int chunkPosY, byte innerX, byte innerY, CellRegisterEntry cellRegisterEntry) {
        int cellId = world.create();
        world.edit(cellId).create(InfCellComponent.class).set(innerX, innerY, cellRegisterEntry);
        if (cellRegisterEntry.getCellBehavior().getEditEntity() != null) {
            cellRegisterEntry.getCellBehavior().getEditEntity().accept(world, cellId);
        }
        if (cellRegisterEntry.getCellBehavior().getCreateBody() != null) {
            CreatePhysiqueBody.CreatePhysiqueBodyReturn physiqueBody = cellRegisterEntry
                    .getCellBehavior()
                    .getCreateBody()
                    .createPhysiqueBody(
                            physicWorld,
                            bodyDef,
                            fixtureDef,
                            MapManager.getWorldPos(chunkPosX, innerX),
                            MapManager.getWorldPos(chunkPosY, innerY)
                    );
            world.edit(cellId).create(Box2dComponent.class).set(physiqueBody.with(), physiqueBody.height(), physiqueBody.body());
            PhysiqueWorldHelper.resetBodyDef(bodyDef);
            PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        }
        return cellId;
    }

    public void supprimeCell(int cellId) {
        InfCellComponent infCellComponent = mCell.get(cellId);
        if (infCellComponent.cellRegisterEntry.getCellBehavior().getDeleteBody() != null) {
            Box2dComponent box2dComponent = mBox2d.get(cellId);
            Body body = box2dComponent.body;
            infCellComponent.cellRegisterEntry.getCellBehavior().getDeleteBody().accept(physicWorld, body);
        }
        world.delete(cellId);
    }

    public void damneChunkClient(int chunkPosX, int chunkPosY) {
        InfMapComponent infMapComponent = mInfMap.get(world.getRegistered("infMap"));
        int chunkId = getChunk(chunkPosX, chunkPosY, infMapComponent.infChunks);
        supprimerChunkAMap(infMapComponent.infChunks, chunkId);
        supprimeChunk(chunkId);
    }

    public void supprimeChunk(int chunkId) {
        world.delete(chunkId);
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        for (int i = 0; i < infChunkComponent.infCellsId.length; i++) {
            world.getSystem(MapManager.class).supprimeCell(infChunkComponent.infCellsId[i]);
        }
    }

    public int[] ajouterChunkAMap(int[] infChunks, int chunkId) {
        int[] ret = new int[infChunks.length + 1];
        System.arraycopy(infChunks, 0, ret, 0, infChunks.length);
        ret[infChunks.length] = chunkId;
        return ret;
    }

    public int[] supprimerChunkAMap(int[] infChunks, int chunkId) {
        int[] ret = new int[infChunks.length - 1];
        for (int i = 0, j = 0; i < infChunks.length; i++) {
            if (infChunks[i] != chunkId) {
                ret[j++] = infChunks[i];
            }
        }
        return ret;
    }
}
