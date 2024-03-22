package ch.realmtech.server.ecs.system;

import ch.realmtech.server.PhysiqueWorldHelper;
import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.level.cell.Cells;
import ch.realmtech.server.level.cell.CreatePhysiqueBody;
import ch.realmtech.server.level.map.WorldMap;
import ch.realmtech.server.level.worldGeneration.PerlinNoise;
import ch.realmtech.server.registry.CellEntry;
import ch.realmtech.server.serialize.SerializerController;
import ch.realmtech.server.serialize.cell.CellArgs;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
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
    private BodyDef bodyDef;
    @Wire
    private FixtureDef fixtureDef;
    @Wire
    private SerializerController serializerController;
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<SaveMetadataComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<CellBeingMineComponent> mCellBeingMine;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    private ComponentMapper<FaceComponent> mFace;

    public static int getWorldPos(int chunkPos, int innerChunk) {
        return chunkPos * WorldMap.CHUNK_SIZE + innerChunk;
    }

    public int getWorldPosX(CellComponent cellComponent) {
        InfChunkComponent infChunkComponent = mChunk.get(cellComponent.chunkId);
        return getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX());
    }

    public int getWorldPosY(CellComponent cellComponent) {
        InfChunkComponent infChunkComponent = mChunk.get(cellComponent.chunkId);
        return getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY());
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

    public static int getChunkPos(int worldPos) {
        return worldPos < 0 ? -(int) Math.ceil((float)-worldPos / WorldMap.CHUNK_SIZE) : worldPos / WorldMap.CHUNK_SIZE;
    }

    /**
     * @return -1 si le chunk n'a pas été trouvé sinon l'id du chunk.
     */
    public int getChunk(int chunkPosX, int chunkPosY, int[] infChunks) {
        for (int infChunk : infChunks) {
            InfChunkComponent infChunkComponent = mChunk.get(infChunk);
            if (chunkPosX == infChunkComponent.chunkPosX && chunkPosY == infChunkComponent.chunkPosY) {
                return infChunk;
            }
        }
        return -1;
    }

    public int getChunkByWorldPos(int worldPosX, int worldPosY, int[] infChunks) {
        int chunkPosX = getChunkPos(worldPosX);
        int chunkPosY = getChunkPos(worldPosY);
        for (int i = 0; i < infChunks.length; i++) {
            int chunkId = infChunks[i];
            InfChunkComponent infChunkComponent = mChunk.get(chunkId);
            if (chunkPosX == infChunkComponent.chunkPosX && chunkPosY == infChunkComponent.chunkPosY) {
                return chunkId;
            }
        }
        return -1;
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
            CellComponent cellComponent = mCell.get(cells[i]);
            if (cellComponent.getInnerPosX() == innerX && cellComponent.getInnerPosY() == innerY && cellComponent.cellRegisterEntry.getCellBehavior().getLayer() == layer) {
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
            CellComponent cellComponent = mCell.get(cells[i]);
            if (cellComponent.getInnerPosX() == innerChunkX && cellComponent.getInnerPosY() == innerChunkY && cellComponent.cellRegisterEntry.getCellBehavior().getLayer() == layer) {
                ret = cells[i];
                break;
            }
        }
        return ret;
    }

    public int newCellInChunk(int chunkId, CellArgs cellArgs) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int cellId = newCell(chunkId, infChunkComponent.chunkPosX, infChunkComponent.chunkPosY, cellArgs);
        int[] newCellsArray = new int[infChunkComponent.infCellsId.length + 1];
        System.arraycopy(infChunkComponent.infCellsId, 0, newCellsArray, 0, infChunkComponent.infCellsId.length);
        newCellsArray[newCellsArray.length - 1] = cellId;
        infChunkComponent.infCellsId = newCellsArray;
        return cellId;
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
        deleteCell(cellId);
    }
    public int generateNewChunk(SaveMetadataComponent saveMetadataComponent, int chunkPosX, int chunkPosY) {
        int chunkId = world.create();
        List<Integer> cellsId = new ArrayList<>(WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE);
        for (short i = 0; i < WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE; i++) {
            int[] cells = generateNewCells(saveMetadataComponent, chunkId, chunkPosX, chunkPosY, i);
            for (int j = 0; j < cells.length; j++) {
                cellsId.add(cells[j]);
            }
        }
        InfChunkComponent infChunkComponent = world.edit(chunkId).create(InfChunkComponent.class);
        infChunkComponent.set(chunkPosX, chunkPosY, cellsId.stream().mapToInt(x -> x).toArray());
        return chunkId;
    }

    private int[] generateNewCells(SaveMetadataComponent saveMetadataComponent, int chunkId, int chunkPosX, int chunkPosY, short index) {
        byte innerChunkX = MapManager.getInnerChunkX(index);
        byte innerChunkY = MapManager.getInnerChunkY(index);
        int worldX = MapManager.getWorldPos(chunkPosX, innerChunkX);
        int worldY = MapManager.getWorldPos(chunkPosY, innerChunkY);
        PerlinNoise perlinNoise = saveMetadataComponent.perlinNoise;
        CellEntry[] cellRegisterEntries = perlinNoise.generateCell(worldX, worldY);
        final int[] cellIds = new int[(int) Arrays.stream(cellRegisterEntries).filter(Objects::nonNull).count()];
        for (int i = 0; i < cellRegisterEntries.length; i++) {
            CellEntry cellRegisterEntry = cellRegisterEntries[i];
            if (cellRegisterEntry != null) {
                cellIds[i] = newCell(chunkId, chunkPosX, chunkPosY, new CellArgs(cellRegisterEntry, Cells.getInnerChunkPos(innerChunkX, innerChunkY)));
            }
        }
        return cellIds;
    }

    public int newCell(int chunkId, int chunkPosX, int chunkPosY, CellArgs cellArgs) {
        int cellId = world.create();
        byte innerX = Cells.getInnerChunkPosX(cellArgs.getInnerChunk());
        byte innerY = Cells.getInnerChunkPosY(cellArgs.getInnerChunk());
        CellEntry cellRegisterEntry = cellArgs.getCellRegisterEntry();
        world.edit(cellId).create(CellComponent.class).set(innerX, innerY, cellRegisterEntry, chunkId);
        cellArgs.getEditEntityArgs()
                .or(() -> cellRegisterEntry.getCellBehavior().getEditEntity())
                .or(cellRegisterEntry::getEditEntity)
                .ifPresent((editEntityArg) -> editEntityArg.createEntity(world.getRegistered("executeOnContext"), cellId));
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

    public void setCell(int worldPosX, int worldPosY, byte layer, CellArgs cellArgs) {
        int chunkId = getChunkByWorldPos(worldPosX, worldPosY, getInfMap().infChunks);
        if (chunkId == -1) {
            logger.info("Can not set cell, chunk id not found. worldPosX: {}, worldPosY: {}", worldPosX, worldPosY);
            return;
        }
        int chunkPosX = getChunkPos(worldPosX);
        int chunkPosY = getChunkPos(worldPosY);
        InfChunkComponent chunkComponent = mChunk.get(chunkId);

        byte innerChunkX = getInnerChunk(worldPosX);
        byte innerChunkY = getInnerChunk(worldPosY);
        int cellId = getCell(chunkId, innerChunkX, innerChunkY, layer);

        if (cellId == -1) {
            logger.info("Can not set cell, cell id not found. worldPosX: {}, worldPosY: {}, layer: {}", worldPosX, worldPosY, layer);
            return;
        }
        int index = Arrays.stream(chunkComponent.infCellsId).boxed().toList().indexOf(cellId);

        deleteCell(cellId);
        int newCellId = newCell(chunkId, chunkPosX, chunkPosY, cellArgs);

        chunkComponent.infCellsId[index] = newCellId;
    }

    public void deleteCell(int cellId) {
        CellComponent cellComponent = mCell.get(cellId);
        if (cellComponent.cellRegisterEntry.getCellBehavior().getDeleteBody() != null) {
            Box2dComponent box2dComponent = mBox2d.get(cellId);
            Body body = box2dComponent.body;
            cellComponent.cellRegisterEntry.getCellBehavior().getDeleteBody().accept(physicWorld, body);
        }
        cellComponent.cellRegisterEntry.getCellBehavior().getEditEntity()
                .or(() -> cellComponent.cellRegisterEntry.getEditEntity())
                .ifPresent((editEntity) -> {
                    ExecuteOnContext executeOnContext = world.getRegistered("executeOnContext");
                    editEntity.deleteEntity(executeOnContext, cellId);
                });
        world.delete(cellId);
    }

    public void damneChunkClient(int chunkPosX, int chunkPosY) {
        InfMapComponent infMapComponent = getInfMap();
        int chunkId = getChunk(chunkPosX, chunkPosY, infMapComponent.infChunks);
        infMapComponent.infChunks = supprimerChunkAMap(infMapComponent.infChunks, chunkId);
        supprimeChunk(chunkId);
    }

    public InfMapComponent getInfMap() {
        return mInfMap.get(world.getSystem(TagManager.class).getEntityId("infMap"));
    }

    public void supprimeChunk(int chunkId) {
        world.delete(chunkId);
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        for (int i = 0; i < infChunkComponent.infCellsId.length; i++) {
            world.getSystem(MapManager.class).deleteCell(infChunkComponent.infCellsId[i]);
        }
    }

    public int[] ajouterChunkAMap(int[] currentChunks, int[] newChunkId, int[] oldChunkId) {
        List<Integer> realNewChunkIdList = new ArrayList<>(newChunkId.length);
        loop:
        for (int i = 0; i < newChunkId.length; i++) {
            for (int j = 0; j < currentChunks.length; j++) {
                if (currentChunks[j] == newChunkId[i]) {
                    continue loop;
                }
            }
            realNewChunkIdList.add(newChunkId[i]);
        }
        int[] realNewChunkId = new int[realNewChunkIdList.size()];
        for (int i = 0; i < realNewChunkIdList.size(); i++) {
            realNewChunkId[i] = realNewChunkIdList.get(i);
        }

        int[] ret = new int[currentChunks.length + realNewChunkId.length - oldChunkId.length];
        System.arraycopy(realNewChunkId, 0, ret, 0, realNewChunkId.length);

        int r = realNewChunkId.length;
        int c = 0;
        loop:
        while (r < ret.length) {
            for (int o = 0; o < oldChunkId.length; o++) {
                if (oldChunkId[o] == currentChunks[c]) {
                    ++c;
                    continue loop;
                }
            }
            ret[r++] = currentChunks[c++];
        }
        return ret;
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

    public void chunkAMounter(SerializedApplicationBytes applicationChunkBytes) {
        int chunkId = serializerController.getChunkSerializerController().decode(applicationChunkBytes);
        InfMapComponent infMapComponent = getInfMap();
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

    public void chunkARemplacer(int chunkPosX, int chunkPosY, SerializedApplicationBytes chunkApplicationBytes, int oldChunkPosX, int oldChunkPosY) {
        try {
            InfMapComponent infMap = getInfMap();
            int oldChunkId = getChunk(oldChunkPosX, oldChunkPosY, infMap.infChunks);
            if (oldChunkId == -1) throw new NoSuchElementException();
            int chunkId = serializerController.getChunkSerializerController().decode(chunkApplicationBytes);
            replaceChunk(infMap.infChunks, oldChunkId, chunkId);
            supprimeChunk(oldChunkId);
        } catch (NoSuchElementException e) {
            logger.error("Le chunk {},{} n'a pas pu être remplacer par {},{}. Tous les chunks {}", oldChunkPosX, oldChunkPosY, chunkPosX, chunkPosY, Arrays.stream(getInfMap().infChunks).mapToObj(chunkId -> {
                InfChunkComponent infChunkComponent = mChunk.get(chunkId);
                return infChunkComponent.chunkPosX + "," + infChunkComponent.chunkPosY;
            }).collect(Collectors.toList()));
        }
    }

    public static float getTexture01coordinate(float gameCoordinate) {
        int worldPos = MapManager.getWorldPos(gameCoordinate);
        return gameCoordinate - (float) worldPos;
    }

    public void rotateCellFace(int cellId, byte faceToRotate) {
        mFace.get(cellId).setFace(faceToRotate);
    }
}
