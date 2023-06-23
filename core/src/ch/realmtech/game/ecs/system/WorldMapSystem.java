package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.DelayedIteratingSystem;
import com.badlogic.gdx.Gdx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@All(InfMapComponent.class)
public class WorldMapSystem extends DelayedIteratingSystem {
    private final static String TAG = WorldMapSystem.class.getSimpleName();
    @Wire(name = "context")
    RealmTech context;
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<PositionComponent> mPosition;
    private final static int RENDER_DISTANCE = 6;
    private int[] ancienneChunkPos = null;
    private final static float INITALE_DELAY = 0.005f;
    private float delay = INITALE_DELAY;

    protected void process(int mapId) {
        int playerId = context.getEcsEngine().getPlayerId();
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        PositionComponent positionPlayerComponent = mPosition.get(playerId);
        int chunkPosX = getChunkX((int) positionPlayerComponent.x);
        int chunkPosY = getChunkY((int) positionPlayerComponent.y);
        if (ancienneChunkPos == null || !(ancienneChunkPos[0] == chunkPosX && ancienneChunkPos[1] == chunkPosY)) {
            List<Integer> chunkADamner = new LinkedList<>();

            for (int i = 0; i < infMapComponent.infChunks.length; i++) {
                if (!chunkEstDansLaRenderDistance(infMapComponent.infChunks[i], chunkPosX, chunkPosY)) {
                    chunkADamner.add(infMapComponent.infChunks[i]);
                }
            }
            int indexDamner = 0;
            for (int i = -RENDER_DISTANCE + chunkPosX; i <= RENDER_DISTANCE + chunkPosX; i++) {
                for (int j = -RENDER_DISTANCE + chunkPosY; j <= RENDER_DISTANCE + chunkPosY; j++) {
                    boolean trouve = false;
                    for (int k = 0; k < infMapComponent.infChunks.length; k++) {
                        InfChunkComponent infChunkComponent = mChunk.get(infMapComponent.infChunks[k]);
                        if (infChunkComponent.chunkPossX == i && infChunkComponent.chunkPossY == j) {
                            trouve = true;
                            break;
                        }
                    }
                    if (!trouve) {
                        int newChunkId = getOrGenerateChunk(mapId, i, j);
                        if (indexDamner < chunkADamner.size()) {
                            Integer oldChunk = chunkADamner.get(indexDamner++);
                            replaceChunk(infMapComponent.infChunks, oldChunk, newChunkId);
                            damneChunk(oldChunk);
                        } else {
                            infMapComponent.infChunks = ajouterChunkAMap(infMapComponent.infChunks, newChunkId);
                        }
                        if (ancienneChunkPos != null) {
                            return;
                        }
                    }
                }
            }
        }
        if (ancienneChunkPos == null) {
            ancienneChunkPos = new int[2];
        }
        ancienneChunkPos[0] = chunkPosX;
        ancienneChunkPos[1] = chunkPosY;
    }

    private void damneChunk(int chunkId) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        world.delete(chunkId);
        for (int i = 0; i < infChunkComponent.infCellsId.length; i++) {
            world.delete(infChunkComponent.infCellsId[i]);
        }
    }

    private boolean chunkEstDansLaRenderDistance(int chunkId, int posX, int posY) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int dstX = Math.abs(posX - infChunkComponent.chunkPossX);
        int dstY = Math.abs(posY - infChunkComponent.chunkPossY);
        return dstX <= RENDER_DISTANCE && dstY <= RENDER_DISTANCE;
    }

    private int getOrGenerateChunk(int mapId, int chunkX, int chunkY) {
        InfMetaDonneesComponent infMetaDonneesComponent = mMetaDonnees.get(mInfMap.get(mapId).infMetaDonnees);
        int chunkId;
        try {
            chunkId = context.getEcsEngine().readSavedInfChunk(chunkX, chunkY, infMetaDonneesComponent.saveName);
        } catch (FileNotFoundException e) {
            chunkId = generateNewChunk(mapId, chunkX, chunkY);
            try {
                context.getEcsEngine().saveInfChunk(chunkId, infMetaDonneesComponent.saveName);
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

    public static int getWorldPossX(int chunkPossX, int innerChunkX) {
        return chunkPossX * WorldMap.CHUNK_SIZE + innerChunkX;
    }

    public static int getWorldPossY(int chunkPossY, int innerChunkY) {
        return chunkPossY * WorldMap.CHUNK_SIZE + innerChunkY;
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
            int[] cells = generateNewCell(infMapComponent.infMetaDonnees, chunkPosX, chunkPosY, i);
            for (int j = 0; j < cells.length; j++) {
                cellsId.add(cells[j]);
            }
        }
        InfChunkComponent infChunkComponent = world.edit(chunkId).create(InfChunkComponent.class);
        infChunkComponent.set(chunkPosX, chunkPosY, cellsId.stream().mapToInt(x -> x).toArray());
        return chunkId;
    }

    private int[] generateNewCell(int metaDonnees, int chunkPosX, int chunkPosY, short index) {
        byte innerChunkX = getInnerChunkX(index);
        byte innerChunkY = getInnerChunkY(index);
        int worldX = getWorldPossX(chunkPosX, innerChunkX);
        int worldY = getWorldPossY(chunkPosY, innerChunkY);
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

    /**
     * Récupère une position Y dans un chunk via la position Y du monde.
     *
     * @param worldY La position Y dans le monde.
     * @return La position Y dans le chunk.
     */
    public static byte getInnerChunkY(int worldY) {
        if (worldY < 0) {
            return (byte) (worldY % WorldMap.CHUNK_SIZE + WorldMap.CHUNK_SIZE);
        } else {
            return (byte) (worldY % WorldMap.CHUNK_SIZE);
        }
    }

    /**
     * Récupère une position X dans un chunk via la position X du monde.
     *
     * @param worldX La position X dans le monde.
     * @return La position X dans le chunk.
     */
    public static byte getInnerChunkX(int worldX) {
        if (worldX < 0) {
            return (byte) (worldX % WorldMap.CHUNK_SIZE + WorldMap.CHUNK_SIZE);
        } else {
            return (byte) (worldX % WorldMap.CHUNK_SIZE);
        }
    }

    /**
     * @param index l'index d'un tableau
     * @return la position x dans le chunk
     */
    public static byte getInnerChunkX(short index) {
        return (byte) Math.abs(index % WorldMap.CHUNK_SIZE);
    }

    /**
     * @param index l'index d'un tableau
     * @return la position y dans le chunk
     */

    public static byte getInnerChunkY(short index) {
        return (byte) Math.abs(index / WorldMap.CHUNK_SIZE);
    }

    public static int getChunkX(int worldX) {
        return (worldX < 0 ? worldX - WorldMap.CHUNK_SIZE : worldX) / WorldMap.CHUNK_SIZE;
    }

    public static int getChunkY(int worldY) {
        return (worldY < 0 ? worldY - WorldMap.CHUNK_SIZE : worldY) / WorldMap.CHUNK_SIZE;
    }

    public int getCell(int[] chunks, int worldPosX, int worldPosY) {
        int chunk = getChunk(chunks, worldPosX, worldPosY);
        int ret = -1;
        if (chunk != -1) {
            int[] cells = mChunk.get(chunk).infCellsId;
            byte innerChunkX = getInnerChunkX(worldPosX);
            byte innerChunkY = getInnerChunkY(worldPosY);
            for (int i = 0; i < cells.length; i++) {
                InfCellComponent infCellComponent = mCell.get(cells[i]);
                if (infCellComponent.posX == innerChunkX && infCellComponent.posY == innerChunkY) {
                    ret = cells[i];
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * @param worldPosX
     * @param worldPosY
     * @return chunk id. -1 si pas trouvé
     */
    public int getChunk(int[] chunks, int worldPosX, int worldPosY) {
        int ret = -1;
        int chunkX = getChunkX(worldPosX);
        int chunkY = getChunkY(worldPosY);
        for (int i = 0; i < chunks.length; i++) {
            InfChunkComponent infChunkComponent = mChunk.get(chunks[i]);
            if (infChunkComponent.chunkPossX == chunkX && infChunkComponent.chunkPossY == chunkY) {
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
}
