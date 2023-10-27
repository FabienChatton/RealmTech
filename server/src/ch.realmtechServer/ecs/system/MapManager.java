package ch.realmtechServer.ecs.system;

import ch.realmtechServer.PhysiqueWorldHelper;
import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.level.cell.Cells;
import ch.realmtechServer.level.cell.CreatePhysiqueBody;
import ch.realmtechServer.level.map.WorldMap;
import ch.realmtechServer.level.worldGeneration.PerlinNoise;
import ch.realmtechServer.options.DataCtrl;
import ch.realmtechServer.registery.CellRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class MapManager extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(MapManager.class);
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
     * @param worldPos La position dans le monde.
     * @return La position dans le chunk.
     */
    public static byte getInnerChunk(int worldPos) {
        if (worldPos < 0) {
            return (byte) ((worldPos % WorldMap.CHUNK_SIZE + WorldMap.CHUNK_SIZE) % WorldMap.CHUNK_SIZE);
        } else {
            return (byte) (worldPos % WorldMap.CHUNK_SIZE);
        }
    }

    public static int getWorldPos(float gameCoordinate) {
        return  (int) (gameCoordinate < 0 ? gameCoordinate - 1 : Math.floor(gameCoordinate));
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
        return worldPos < 0 ? -(int) Math.ceil((float)-worldPos / WorldMap.CHUNK_SIZE) : worldPos / WorldMap.CHUNK_SIZE;
    }

    @Deprecated
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

    public int getChunk(int[] chunks, float gameCoordinateX, float gameCoordinateY) {
        int ret = -1;
        int chunkX = MapManager.getChunkPos(gameCoordinateX);
        int chunkY = MapManager.getChunkPos(gameCoordinateY);
        for (int i = 0; i < chunks.length; i++) {
            InfChunkComponent infChunkComponent = mChunk.get(chunks[i]);
            if (infChunkComponent.chunkPosX == chunkX && infChunkComponent.chunkPosY == chunkY) {
                ret = chunks[i];
                break;
            }
        }
        return ret;
    }

    public int getTopCell(int chunk, byte innerX, byte innerY) {
        int ret = -1;
        for (byte i = Cells.Layer.BUILD_DECO.layer; i >= 0; i--) {
            int cellId = getCell(chunk, innerX, innerY, i);
            if (cellId != -1) {
                ret = cellId;
                break;
            }
        }
        return ret;
    }

    public int getCell(int chunk, byte innerX, byte innerY, byte layer) {
        int ret = -1;
        int[] cells = mChunk.get(chunk).infCellsId;
        for (int i = 0; i < cells.length; i++) {
            InfCellComponent infCellComponent = mCell.get(cells[i]);
            if (infCellComponent.getInnerPosX() == innerX && infCellComponent.getInnerPosY() == innerY && infCellComponent.cellRegisterEntry.getCellBehavior().getLayer() == layer) {
                ret = cells[i];
                break;
            }
        }
        return ret;
    }
    public int getCell(int chunkId, int worldPosX, int worldPosY, byte layer) {
        int ret = -1;
        int[] cells = mChunk.get(chunkId).infCellsId;
        byte innerChunkX = MapManager.getInnerChunk(worldPosX);
        byte innerChunkY = MapManager.getInnerChunk(worldPosY);
        for (int i = 0; i < cells.length; i++) {
            InfCellComponent infCellComponent = mCell.get(cells[i]);
            if (infCellComponent.getInnerPosX() == innerChunkX && infCellComponent.getInnerPosY() == innerChunkY && infCellComponent.cellRegisterEntry.getCellBehavior().getLayer() == layer) {
                ret = cells[i];
                break;
            }
        }
        return ret;
    }

    private void newCellInChunk(int chunkId, CellRegisterEntry cellRegisterEntry, byte innerX, byte innerY) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int cellId = world.getSystem(MapManager.class).newCell(chunkId, infChunkComponent.chunkPosX, infChunkComponent.chunkPosY, innerX, innerY, cellRegisterEntry);
        int[] newCellsArray = new int[infChunkComponent.infCellsId.length + 1];
        System.arraycopy(infChunkComponent.infCellsId, 0, newCellsArray, 0, infChunkComponent.infCellsId.length);
        newCellsArray[newCellsArray.length - 1] = cellId;
        infChunkComponent.infCellsId = newCellsArray;
    }

    public int findChunk(int[] chunks, int cellId) {
        for (int chunk : chunks) {
            InfChunkComponent infChunkComponent = mChunk.get(chunk);
            for (int cell : infChunkComponent.infCellsId) {
                if (cell == cellId) {
                    return chunk;
                }
            }
        }
        throw new NoSuchElementException("Le chunk qui correspond à la cellule n'a pas été trouvé");
    }

    public boolean placeItemToBloc(final int playerId, final int button, final int[] chunks, final float gameCoordinateX, final float gameCoordinateY, int selectedItem) {
//        if (selectedItem > 0) {
//            final ItemRegisterEntry selectedItemEntry = mItem.get(world.getSystem(ItemBarManager.class).getSelectItem()).itemRegisterEntry;
//            if (selectedItemEntry.getItemBehavior().getPlaceCell() != null) {
//                final byte innerX = getInnerChunk(gameCoordinateX);
//                final byte innerY = getInnerChunk(gameCoordinateY);
//                final int chunkId = getChunk(chunks, gameCoordinateX, gameCoordinateY);
//                if (getCell(chunkId, innerX, innerY, selectedItemEntry.getItemBehavior().getPlaceCell().getCellBehavior().getLayer()) == -1) {
//                    newCellInChunk(chunkId, selectedItemEntry.getItemBehavior().getPlaceCell(), innerX, innerY);
//                    return true;
//                }
//            }
//        }
        return false;
    }

    public boolean addCellBeingMine(int cellId) {
        boolean ret = false;
        if (cellId != -1 && !mCellBeingMine.has(cellId)) {
            mCellBeingMine.create(cellId).set(0, mCell.get(cellId).cellRegisterEntry.getCellBehavior().getBreakStepNeed());
            ret = true;
        }
        return ret;
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
    public int generateNewChunk(InfMetaDonneesComponent infMetaDonneesComponent, int chunkPosX, int chunkPosY) {
        int chunkId = world.create();
        List<Integer> cellsId = new ArrayList<>(WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE);
        for (short i = 0; i < WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE; i++) {
            int[] cells = generateNewCells(infMetaDonneesComponent, chunkId, chunkPosX, chunkPosY, i);
            for (int j = 0; j < cells.length; j++) {
                cellsId.add(cells[j]);
            }
        }
        InfChunkComponent infChunkComponent = world.edit(chunkId).create(InfChunkComponent.class);
        infChunkComponent.set(chunkPosX, chunkPosY, cellsId.stream().mapToInt(x -> x).toArray());
        return chunkId;
    }

    private int[] generateNewCells(InfMetaDonneesComponent infMetaDonneesComponent, int chunkId, int chunkPosX, int chunkPosY, short index) {
        byte innerChunkX = MapManager.getInnerChunkX(index);
        byte innerChunkY = MapManager.getInnerChunkY(index);
        int worldX = MapManager.getWorldPos(chunkPosX, innerChunkX);
        int worldY = MapManager.getWorldPos(chunkPosY, innerChunkY);
        PerlinNoise perlinNoise = infMetaDonneesComponent.perlinNoise;
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
        InfMapComponent infMapComponent = getInfMap();
        int chunkId = getChunk(chunkPosX, chunkPosY, infMapComponent.infChunks);
        infMapComponent.infChunks = supprimerChunkAMap(infMapComponent.infChunks, chunkId);
        supprimeChunk(chunkId);
    }

    private InfMapComponent getInfMap() {
        return mInfMap.get(world.getSystem(TagManager.class).getEntityId("infMap"));
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

    public void replaceChunk(int[] chunks, int oldChunk, int newChunkId) {
        for (int i = 0; i < chunks.length; i++) {
            if (chunks[i] == oldChunk) {
                chunks[i] = newChunkId;
                return;
            }
        }
    }

    public void chunkAMounter(int chunkPosX, int chunkPosY, byte[] chunkBytes) {
        int chunkId = InfChunkComponent.fromByte(world, chunkBytes, chunkPosX, chunkPosY);
        Entity infMapEntity = world.getSystem(TagManager.class).getEntity("infMap");
        InfMapComponent infMapComponent = infMapEntity.getComponent(InfMapComponent.class);
        infMapComponent.infChunks = world.getSystem(MapManager.class).ajouterChunkAMap(infMapComponent.infChunks, chunkId);
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

    public void chunkARemplacer(int chunkPosX, int chunkPosY, byte[] chunkBytes, int oldChunkPosX, int oldChunkPosY) {
        try {
            InfMapComponent infMap = getInfMap();
            int oldChunkId = getChunk(oldChunkPosX, oldChunkPosY, infMap.infChunks);
            int chunkId = InfChunkComponent.fromByte(world, chunkBytes, chunkPosX, chunkPosY);
            replaceChunk(infMap.infChunks, oldChunkId, chunkId);
            supprimeChunk(oldChunkId);
        } catch (NoSuchElementException e) {
            logger.error("Le chunk {},{} n'a pas pu être remplacer par {},{}. Tous les chunks {}", oldChunkPosX, oldChunkPosY, chunkPosX, chunkPosY, Arrays.stream(getInfMap().infChunks).mapToObj(chunkId -> {
                InfChunkComponent infChunkComponent = mChunk.get(chunkId);
                return infChunkComponent.chunkPosX + "," + infChunkComponent.chunkPosY;
            }).collect(Collectors.toList()));
        }
    }
}
