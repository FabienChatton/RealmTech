package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.level.cell.BreakCell;
import ch.realmtech.game.level.cell.Cells;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import ch.realmtech.game.registery.CellRegisterEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.DelayedIteratingSystem;
import com.badlogic.gdx.Gdx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@All(InfMapComponent.class)
public class MapSystem extends DelayedIteratingSystem {
    private final static String TAG = MapSystem.class.getSimpleName();
    @Wire(name = "context")
    RealmTech context;
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PlayerComponent> mPlayer;
    private int[] ancienneChunkPos = null;
    private final static float INITALE_DELAY = 0.005f;
    private float delay = INITALE_DELAY;

    protected void process(int mapId) {
        int playerId = context.getEcsEngine().getPlayerId();
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        PositionComponent positionPlayerComponent = mPosition.get(playerId);
        int chunkPosX = getChunkPos((int) positionPlayerComponent.x);
        int chunkPosY = getChunkPos((int) positionPlayerComponent.y);
        if (ancienneChunkPos == null || !(ancienneChunkPos[0] == chunkPosX && ancienneChunkPos[1] == chunkPosY)) {
            List<Integer> chunkADamner = new ArrayList<>(2 * context.getRealmTechDataCtrl().option.renderDistance.get() + 1);

            trouveChunkADamner(infMapComponent, chunkPosX, chunkPosY, chunkADamner);
            int indexDamner = 0;
            for (int i = -context.getRealmTechDataCtrl().option.renderDistance.get() + chunkPosX; i <= context.getRealmTechDataCtrl().option.renderDistance.get() + chunkPosX; i++) {
                for (int j = -context.getRealmTechDataCtrl().option.renderDistance.get() + chunkPosY; j <= context.getRealmTechDataCtrl().option.renderDistance.get() + chunkPosY; j++) {
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
                    if (indexDamner >= context.getRealmTechDataCtrl().option.chunkParUpdate.get()) {
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
        if (ancienneChunkPos == null) {
            ancienneChunkPos = new int[2];
        }
        ancienneChunkPos[0] = chunkPosX;
        ancienneChunkPos[1] = chunkPosY;
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

    private void trouveChunkADamner(InfMapComponent infMapComponent, int chunkPosX, int chunkPosY, List<Integer> chunkADamner) {
        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
            if (!chunkEstDansLaRenderDistance(infMapComponent.infChunks[i], chunkPosX, chunkPosY)) {
                chunkADamner.add(infMapComponent.infChunks[i]);
            }
        }
    }

    private void damneChunk(int chunkId, InfMapComponent infMapComponent) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        try {
            world.getSystem(SaveInfManager.class).saveInfChunk(chunkId, SaveInfManager.getSavePath(mMetaDonnees.get(infMapComponent.infMetaDonnees).saveName));
        } catch (IOException e) {
            Gdx.app.error(TAG, String.format("Le chunk %d,%d n'a pas été sauvegardé correctement", infChunkComponent.chunkPosX, infChunkComponent.chunkPosY), e);
        }
        world.delete(chunkId);
        for (int i = 0; i < infChunkComponent.infCellsId.length; i++) {
            world.delete(infChunkComponent.infCellsId[i]);
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
        world.delete(cellId);
    }

    private boolean chunkEstDansLaRenderDistance(int chunkId, int posX, int posY) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int dstX = Math.abs(posX - infChunkComponent.chunkPosX);
        int dstY = Math.abs(posY - infChunkComponent.chunkPosY);
        return dstX <= context.getRealmTechDataCtrl().option.renderDistance.get() && dstY <= context.getRealmTechDataCtrl().option.renderDistance.get();
    }

    private int getOrGenerateChunk(int mapId, int chunkX, int chunkY) {
        InfMetaDonneesComponent infMetaDonneesComponent = mMetaDonnees.get(mInfMap.get(mapId).infMetaDonnees);
        int chunkId;
        try {
            chunkId = context.getEcsEngine().readSavedInfChunk(chunkX, chunkY, infMetaDonneesComponent.saveName);
        } catch (FileNotFoundException e) {
            chunkId = generateNewChunk(mapId, chunkX, chunkY);
            try {
                context.getEcsEngine().saveInfChunk(chunkId, SaveInfManager.getSavePath(infMetaDonneesComponent.saveName));
            } catch (IOException ex) {
                Gdx.app.error(TAG, e.getMessage(), e);
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            Gdx.app.error(TAG, e.getMessage(), e);
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
            int[] cells = generateNewCells(infMapComponent.infMetaDonnees, chunkPosX, chunkPosY, i);
            for (int j = 0; j < cells.length; j++) {
                cellsId.add(cells[j]);
            }
        }
        InfChunkComponent infChunkComponent = world.edit(chunkId).create(InfChunkComponent.class);
        infChunkComponent.set(chunkPosX, chunkPosY, cellsId.stream().mapToInt(x -> x).toArray());
        return chunkId;
    }

    private int[] generateNewCells(int metaDonnees, int chunkPosX, int chunkPosY, short index) {
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
                int cellId = world.create();
                cellIds[i] = cellId;
                world.edit(cellId).create(InfCellComponent.class).set(innerChunkX, innerChunkY, cellRegisterEntry);
            }
        }
        return cellIds;
    }

    private void newCellInChunk(InfChunkComponent infChunkComponent, CellRegisterEntry cellRegisterEntry, byte innerX, byte innerY) {
        int cellId = world.create();
        world.edit(cellId).create(InfCellComponent.class).set(innerX, innerY, cellRegisterEntry);
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
            if (infCellComponent.innerPosX == innerChunkX && infCellComponent.innerPosY == innerChunkY && infCellComponent.cellRegisterEntry.getCellBehavior().getLayer() == layer) {
                ret = cells[i];
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
            if (infCellComponent.innerPosX == innerX && infCellComponent.innerPosY == innerY && infCellComponent.cellRegisterEntry.getCellBehavior().getLayer() == layer) {
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

    public void breakTopCell(final int playerId, final int button, final int[] chunks, final float gameCoordinateX, final float gameCoordinateY) {
        final byte innerX = getInnerChunk(gameCoordinateX);
        final byte innerY = getInnerChunk(gameCoordinateY);
        final int chunk = getChunk(chunks, gameCoordinateX, gameCoordinateY);
        final int topCellId = getTopCell(chunk, innerX, innerY);
        if (topCellId != -1) {
            final InfCellComponent infCellComponent = mCell.get(topCellId);
            final BreakCell breakCellEvent = infCellComponent.cellRegisterEntry.getCellBehavior().getBreakCellEvent();
            if (breakCellEvent != null) {
                breakCellEvent.breakCell(world, chunk, topCellId, mItem.get(world.getSystem(ItemBarManager.class).getSelectItem()), mPlayer.get(playerId));
            }
        }
    }

    public boolean placeItemToBloc(final int playerId, final int button, final int[] chunks, final float gameCoordinateX, final float gameCoordinateY, int selectedItem) {
        if (selectedItem > 0) {
            final ItemRegisterEntry selectedItemEntry = mItem.get(world.getSystem(ItemBarManager.class).getSelectItem()).itemRegisterEntry;
            if (selectedItemEntry.getItemBehavior().getPlaceCell() != null) {
                final byte innerX = getInnerChunk(gameCoordinateX);
                final byte innerY = getInnerChunk(gameCoordinateY);
                final int chunk = getChunk(chunks, gameCoordinateX, gameCoordinateY);
                if (getCell(chunk, innerX, innerY, selectedItemEntry.getItemBehavior().getPlaceCell().getCellBehavior().getLayer()) == -1) {
                    newCellInChunk(mChunk.get(chunk), selectedItemEntry.getItemBehavior().getPlaceCell(), innerX, innerY);
                    return true;
                }
            }
        }
        return false;
    }
}
