package ch.realmtechServer.ecs.system;

import ch.realmtechServer.PhysiqueWorldHelper;
import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.level.cell.BreakCell;
import ch.realmtechServer.level.cell.Cells;
import ch.realmtechServer.level.cell.CreatePhysiqueBody;
import ch.realmtechServer.level.map.WorldMap;
import ch.realmtechServer.level.worldGeneration.PerlinNoise;
import ch.realmtechServer.options.DataCtrl;
import ch.realmtechServer.registery.CellRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.DelayedIteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@All(InfMapComponent.class)
public class MapSystem extends DelayedIteratingSystem {
    private final static String TAG = MapSystem.class.getSimpleName();
    private final static Logger logger = LoggerFactory.getLogger(MapSystem.class);
//    @Wire
//    private SoundManager soundManager;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
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
    private ComponentMapper<PlayerConnectionComponent> mPlayerConnection;
    private final static float INITALE_DELAY = 0.005f;
    private float delay = INITALE_DELAY;

    protected void process(int mapId) {
        IntBag players = world.getSystem(PlayerManagerServer.class).getPlayers();
        int[] playersData = players.getData();
        for (int p = 0; p < players.size(); p++) {
            int playerId = playersData[p];
            InfMapComponent infMapComponent = mInfMap.get(mapId);
            PositionComponent positionPlayerComponent = mPosition.get(playerId);
            PlayerConnectionComponent playerConnectionComponent = mPlayerConnection.get(playerId);
            int[] ancienChunkPos = playerConnectionComponent.ancienChunkPos;
            int chunkPosX = getChunkPos((int) positionPlayerComponent.x);
            int chunkPosY = getChunkPos((int) positionPlayerComponent.y);
            if (ancienChunkPos == null || !(ancienChunkPos[0] == chunkPosX && ancienChunkPos[1] == chunkPosY)) {
                List<Integer> chunkADamner = trouveChunkADamner(infMapComponent, chunkPosX, chunkPosY);

                int indexDamner = 0;
                for (int i = -dataCtrl.option.renderDistance.get() + chunkPosX; i <= dataCtrl.option.renderDistance.get() + chunkPosX; i++) {
                    for (int j = -dataCtrl.option.renderDistance.get() + chunkPosY; j <= dataCtrl.option.renderDistance.get() + chunkPosY; j++) {
                        final boolean changement = chunkSansChangement(infMapComponent, i, j);
                        if (changement) {
                            int newChunkId = getOrGenerateChunk(mapId, i, j);
                            if (indexDamner < chunkADamner.size()) {
                                Integer oldChunk = chunkADamner.get(indexDamner++);
                                replaceChunk(infMapComponent.infChunks, oldChunk, newChunkId);
                                damneChunk(oldChunk, infMapComponent);
                            } else {
                                infMapComponent.infChunks = ajouterChunkAMap(infMapComponent.infChunks, newChunkId);
                            }
                        }
                        // la limite d'update de chunk pour ce process est atteint
                        if (indexDamner >= dataCtrl.option.chunkParUpdate.get()) {
                            return;
                        }
                    }
                }
                if (indexDamner < chunkADamner.size()) {
                    for (int i = indexDamner; i < chunkADamner.size(); i++) {
                        final int chunkId = chunkADamner.get(i);
                        damneChunk(chunkId, infMapComponent);
                        infMapComponent.infChunks = supprimerChunkAMap(mapId, chunkId);
                    }
                }
            }
            if (ancienChunkPos == null) {
                ancienChunkPos = new int[2];
            }
            ancienChunkPos[0] = chunkPosX;
            ancienChunkPos[1] = chunkPosY;
        }
    }

    /**
     * Permet de savoir si le chunk n'a pas besoin d'être changé
     */
    private boolean chunkSansChangement(InfMapComponent infMapComponent, int i, int j) {
        boolean trouve = false;
        for (int k = 0; k < infMapComponent.infChunks.length; k++) {
            InfChunkComponent infChunkComponent = mChunk.get(infMapComponent.infChunks[k]);
            if (infChunkComponent.chunkPosX == i && infChunkComponent.chunkPosY == j) {
                trouve = true;
                break;
            }
        }
        return !trouve;
    }

    private List<Integer> trouveChunkADamner(InfMapComponent infMapComponent, int chunkPosX, int chunkPosY) {
        List<Integer> ret = new ArrayList<>(2 * dataCtrl.option.renderDistance.get() + 1);
        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
            if (!chunkEstDansLaRenderDistance(infMapComponent.infChunks[i], chunkPosX, chunkPosY)) {
                ret.add(infMapComponent.infChunks[i]);
            }
        }
        return ret;
    }

    private void damneChunk(int chunkId, InfMapComponent infMapComponent) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        try {
            world.getSystem(SaveInfManager.class).saveInfChunk(chunkId, SaveInfManager.getSavePath(mMetaDonnees.get(infMapComponent.infMetaDonnees).saveName));
        } catch (IOException e) {
            logger.error("Le chunk {},{} n'a pas été sauvegardé correctement", infChunkComponent.chunkPosX, infChunkComponent.chunkPosY);
        }
        world.delete(chunkId);
        for (int i = 0; i < infChunkComponent.infCellsId.length; i++) {
            supprimeCell(infChunkComponent.infCellsId[i]);
        }
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

    private boolean chunkEstDansLaRenderDistance(int chunkId, int posX, int posY) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int dstX = Math.abs(posX - infChunkComponent.chunkPosX);
        int dstY = Math.abs(posY - infChunkComponent.chunkPosY);
        // à remplacer pour une valuer dans une configuration
        return dstX <= 4 /*dataCtrl.option.renderDistance.get()*/ && dstY <= 4 /*dataCtrl.option.renderDistance.get()*/;
    }

    private int getOrGenerateChunk(int mapId, int chunkX, int chunkY) {
        InfMetaDonneesComponent infMetaDonneesComponent = mMetaDonnees.get(mInfMap.get(mapId).infMetaDonnees);
        int chunkId;
        try {
            chunkId = world.getSystem(SaveInfManager.class).readSavedInfChunk(chunkX, chunkY, infMetaDonneesComponent.saveName);
        } catch (FileNotFoundException e) {
            chunkId = generateNewChunk(mapId, chunkX, chunkY);
            try {
                world.getSystem(SaveInfManager.class).saveInfChunk(chunkId, SaveInfManager.getSavePath(infMetaDonneesComponent.saveName));
            } catch (IOException ex) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return chunkId;
    }

    public static int getWorldPos(int chunkPos, int innerChunk) {
        return chunkPos * WorldMap.CHUNK_SIZE + innerChunk;
    }

    /**
     * @param chunkPosX La position X du chunk
     * @param chunkPosY La position Y du chunk
     * @return l'id du nouveau chunk
     */
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
                            getWorldPos(chunkPosX, innerX),
                            getWorldPos(chunkPosY, innerY)
                    );
            world.edit(cellId).create(Box2dComponent.class).set(physiqueBody.with(), physiqueBody.height(), physiqueBody.body());
            PhysiqueWorldHelper.resetBodyDef(bodyDef);
            PhysiqueWorldHelper.resetFixtureDef(fixtureDef);
        }
        return cellId;
    }

    private void supprimeCell(int cellId) {
        InfCellComponent infCellComponent = mCell.get(cellId);
        if (infCellComponent.cellRegisterEntry.getCellBehavior().getDeleteBody() != null) {
            Box2dComponent box2dComponent = mBox2d.get(cellId);
            Body body = box2dComponent.body;
            infCellComponent.cellRegisterEntry.getCellBehavior().getDeleteBody().accept(physicWorld, body);
        }
        world.delete(cellId);
    }

    private int[] generateNewCells(int metaDonnees, int chunkId, int chunkPosX, int chunkPosY, short index) {
        byte innerChunkX = getInnerChunkX(index);
        byte innerChunkY = getInnerChunkY(index);
        int worldX = getWorldPos(chunkPosX, innerChunkX);
        int worldY = getWorldPos(chunkPosY, innerChunkY);
        PerlinNoise perlinNoise = mMetaDonnees.get(metaDonnees).perlinNoise;
        final CellRegisterEntry[] cellRegisterEntries = perlinNoise.generateCell(worldX, worldY);
        final int[] cellIds = new int[(int) Arrays.stream(cellRegisterEntries).filter(Objects::nonNull).count()];
        for (int i = 0; i < cellRegisterEntries.length; i++) {
            CellRegisterEntry cellRegisterEntry = cellRegisterEntries[i];
            if (cellRegisterEntry != null) {
                cellIds[i] = newCell(chunkId, chunkPosX, chunkPosY, innerChunkX, innerChunkY, cellRegisterEntry);
            }
        }
        return cellIds;
    }

    private void newCellInChunk(int chunkId, CellRegisterEntry cellRegisterEntry, byte innerX, byte innerY) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int cellId = newCell(chunkId, infChunkComponent.chunkPosX, infChunkComponent.chunkPosY, innerX, innerY, cellRegisterEntry);
        int[] newCellsArray = new int[infChunkComponent.infCellsId.length + 1];
        System.arraycopy(infChunkComponent.infCellsId, 0, newCellsArray, 0, infChunkComponent.infCellsId.length);
        newCellsArray[newCellsArray.length - 1] = cellId;
        infChunkComponent.infCellsId = newCellsArray;
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

    public int getCell(int[] chunks, int worldPosX, int worldPosY, byte layer) {
        int chunk = getChunk(chunks, worldPosX, worldPosY);
        int ret = -1;
        if (chunk != -1) {
            ret = getCell(chunk, worldPosX, worldPosY, layer);
        }
        return ret;
    }

    public int getCell(int chunkId, int worldPosX, int worldPosY, byte layer) {
        int ret = -1;
        int[] cells = mChunk.get(chunkId).infCellsId;
        byte innerChunkX = getInnerChunk(worldPosX);
        byte innerChunkY = getInnerChunk(worldPosY);
        for (int i = 0; i < cells.length; i++) {
            InfCellComponent infCellComponent = mCell.get(cells[i]);
            if (infCellComponent.getInnerPosX() == innerChunkX && infCellComponent.getInnerPosY() == innerChunkY && infCellComponent.cellRegisterEntry.getCellBehavior().getLayer() == layer) {
                ret = cells[i];
                break;
            }
        }
        return ret;
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

    /**
     * @return chunk id. -1 si pas trouvé
     */
    public int getChunk(int[] chunks, int worldPosX, int worldPosY) {
        int ret = -1;
        int chunkX = getChunkPos(worldPosX);
        int chunkY = getChunkPos(worldPosY);
        for (int i = 0; i < chunks.length; i++) {
            InfChunkComponent infChunkComponent = mChunk.get(chunks[i]);
            if (infChunkComponent.chunkPosX == chunkX && infChunkComponent.chunkPosY == chunkY) {
                ret = chunks[i];
                break;
            }
        }
        return ret;
    }

    public int getChunk(int[] chunks, float gameCoordinateX, float gameCoordinateY) {
        int ret = -1;
        int chunkX = getChunkPos(gameCoordinateX);
        int chunkY = getChunkPos(gameCoordinateY);
        for (int i = 0; i < chunks.length; i++) {
            InfChunkComponent infChunkComponent = mChunk.get(chunks[i]);
            if (infChunkComponent.chunkPosX == chunkX && infChunkComponent.chunkPosY == chunkY) {
                ret = chunks[i];
                break;
            }
        }
        return ret;
    }
    private void replaceChunk(int[] chunks, int oldChunk, int newChunkId) {
        for (int i = 0; i < chunks.length; i++) {
            if (chunks[i] == oldChunk) {
                chunks[i] = newChunkId;
                return;
            }
        }
    }

    private int[] ajouterChunkAMap(int[] infChunks, int chunkId) {
        int[] ret = new int[infChunks.length + 1];
        System.arraycopy(infChunks, 0, ret, 0, infChunks.length);
        ret[infChunks.length] = chunkId;
        return ret;
    }

    private int[] supprimerChunkAMap(int mapId, int chunkId) {
        int[] infChunks = mInfMap.get(mapId).infChunks;
        int[] ret = new int[infChunks.length - 1];
        for (int i = 0, j = 0; i < infChunks.length; i++) {
            if (infChunks[i] != chunkId) {
                ret[j++] = infChunks[i];
            }
        }
        return ret;
    }

    @Override
    protected float getRemainingDelay(int entityId) {
        return delay;
    }

    @Override
    protected void processDelta(int entityId, float accumulatedDelta) {
        delay -= accumulatedDelta;
    }

    @Override
    protected void processExpired(int mapId) {
        process(mapId);
        offerDelay(INITALE_DELAY);
    }

    public int getTopCell(int chunk, byte innerX, byte innerY) {
        int ret = -1;
        for (byte i = Cells.Layer.BUILD_DECO.layer; i >= 0; i--) {
            int cellId = world.getSystem(MapSystem.class).getCell(chunk, innerX, innerY, i);
            if (cellId != -1) {
                ret = cellId;
                break;
            }
        }
        return ret;
    }


    public void breakCell(int chunk, int cellId, int playerId) {
        InfCellComponent infCellComponent = mCell.get(cellId);
        BreakCell breakCellEvent = infCellComponent.cellRegisterEntry.getCellBehavior().getBreakCellEvent();
        if (breakCellEvent != null) {
//            if (breakCellEvent.breakCell(world, chunk, cellId, mItem.get(world.getSystem(ItemBarManager.class).getSelectItem()), mPlayer.get(playerId))) {
//                soundManager.playCellBreak();
//            }
        }
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

//    public void interagieClickDroit(int playerId, int button, int[] infChunks, float x, float y, int selectItem) {
//        final int chunk = getChunk(infChunks, x, y);
//        final int topCell = getTopCell(chunk, getInnerChunk(x), getInnerChunk(y));
//        InfCellComponent infCellComponent = mCell.get(topCell);
//        if (infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit() != null) {
//            infCellComponent.cellRegisterEntry.getCellBehavior().getInteragieClickDroit().accept(world, topCell);
//        } else {
//            if (world.getSystem(MapSystem.class).placeItemToBloc(context.getEcsEngine().getPlayerId(), button, context.getEcsEngine().getWorld().getMapper(InfMapComponent.class).get(context.getEcsEngine().getMapId()).infChunks, x, y, context.getSystem(ItemBarManager.class).getSelectItem())) {
//                world.getSystem(InventoryManager.class).removeOneItem(context.getSystem(ItemBarManager.class).getSelectStack());
//            }
//        }
//    }

    public boolean addCellBeingMine(int cellId) {
        boolean ret = false;
        if (!mCellBeingMine.has(cellId)) {
            mCellBeingMine.create(cellId).set(0, mCell.get(cellId).cellRegisterEntry.getCellBehavior().getBreakStepNeed());
            ret = true;
        }
        return ret;
    }

//    public static Vector3 getGameCoordinate(RealmTech context, Vector2 screenCoordinate) {
//        return context.getGameStage().getCamera().unproject(new Vector3(screenCoordinate, 0));
//    }
//
//    public static int getTopCell(RealmTech context, int chunk, Vector2 screenCoordinate) {
//        Vector3 gameCoordinate = getGameCoordinate(context, screenCoordinate);
//        return context.getEcsEngine().getWorld().getSystem(MapSystem.class).getTopCell(chunk, getInnerChunk(gameCoordinate.x), getInnerChunk(gameCoordinate.y));
//    }
//
//    public static int getChunk(RealmTech context, Vector2 screenCoordinate) {
//        int[] infChunks = getChunkInUse(context);
//        Vector3 gameCoordinate = getGameCoordinate(context, screenCoordinate);
//        return context.getEcsEngine().getWorld().getSystem(MapSystem.class).getChunk(infChunks, gameCoordinate.x, gameCoordinate.y);
//    }
//
//    public static int[] getChunkInUse(RealmTech context) {
//        return context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;
//    }
}
