package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import ch.realmtech.game.mod.RealmTechCoreCell;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@All(InfMapComponent.class)
public class WorldMapSystem extends IteratingSystem {
    private final static String TAG = WorldMapSystem.class.getSimpleName();
    @Wire(name = "context")
    RealmTech context;
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<PositionComponent> mPosition;
    private final static int RENDER_DISTANCE = 5;

    @Override
    protected void process(int mapId) {
        int playerId = context.getEcsEngine().getPlayerId();
        InfMapComponent infMapComponent = mInfMap.get(mapId);
        InfMetaDonneesComponent infMetaDonneesComponent = mMetaDonnees.get(infMapComponent.infMetaDonnees);
        PositionComponent positionPlayerComponent = mPosition.get(playerId);
        int chunkPosX = getChunkX((int) positionPlayerComponent.x);
        int chunkPosY = getChunkY((int) positionPlayerComponent.y);
        HashMap<Integer, HashMap<Integer, Integer>> chunkACharger = new HashMap<>();
        // remplie la map qui représente tous les chunks dans
        for (int i = -RENDER_DISTANCE + chunkPosX; i <= RENDER_DISTANCE + chunkPosX; i++) {
            for (int j = -RENDER_DISTANCE + chunkPosY; j <= RENDER_DISTANCE + chunkPosY; j++) {
                HashMap<Integer, Integer> chunkY;
                if (chunkACharger.get(i) == null) {
                    chunkY = new HashMap<>();
                    chunkACharger.put(i, chunkY);
                } else {
                    chunkY = chunkACharger.get(i);
                }
                chunkY.put(j, 0);
            }
        }
        // trouve les chunk à de charger
        List<Integer> chunkADeCharger = new LinkedList<>();
        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
            int chunkId = infMapComponent.infChunks[i];
            InfChunkComponent infChunkComponent = mChunk.get(chunkId);
            if (!chunkEstDansLaRenderDistance(chunkId, chunkPosX, chunkPosY)) {
                chunkADeCharger.add(chunkId);
            }
        }
        // met les chunks à conserver
        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
            int chunkId = infMapComponent.infChunks[i];
            if (!chunkADeCharger.contains(chunkId)) {
                InfChunkComponent infChunkComponent = mChunk.get(chunkId);
                chunkACharger.get(infChunkComponent.chunkPossX).put(infChunkComponent.chunkPossY, chunkId);
            }
        }
        // ajoute les chunk manquant
        for (int chunkX : chunkACharger.keySet()) {
            for (int chunkY : chunkACharger.get(chunkX).keySet()) {
                int chunkId = chunkACharger.get(chunkX).get(chunkY);
                if (chunkId == 0) {
                    chunkACharger.get(chunkX).put(chunkY, getOrGenerateChunk(mapId, chunkX, chunkY));
                }
            }
        }
        List<Integer> chunkFinal = new LinkedList<>();
        for (int chunkX : chunkACharger.keySet()) {
            for (int chunkY : chunkACharger.get(chunkX).keySet()) {
                chunkFinal.add(chunkACharger.get(chunkX).get(chunkY));
            }
        }
        infMapComponent.infChunks = chunkFinal.stream().mapToInt(x -> x).toArray();


//
//
//        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
//            InfChunkComponent infChunkComponent = mChunk.get(infMapComponent.infChunks[i]);
//            if (!(infChunkComponent.chunkPossX == chunkPosX && infChunkComponent.chunkPossY == chunkPosY)) {
//                int chunkId = getOrGenerateChunk(mapId, chunkPosX, chunkPosY);
//                replaceChunk(mapId, infMapComponent.infChunks[i], chunkId);
//            }
//        }

    }

    private boolean chunkEstDansLaRenderDistance(int chunkId, int posX, int posY) {
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        int chunkX = infChunkComponent.chunkPossX;
        int chunkY = infChunkComponent.chunkPossY;
        return Vector2.dst2(chunkX, chunkY, posX, posY) < RENDER_DISTANCE;
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
            cellsId.add(generateNewCell(infMapComponent.infMetaDonnees, chunkPosX, chunkPosY, i));
        }
        world.edit(chunkId).create(InfChunkComponent.class).set(chunkPosX, chunkPosY, cellsId.stream().mapToInt(x -> x).toArray());
        return chunkId;
    }

    private int generateNewCell(int metaDonnees, int chunkPosX, int chunkPosY, short index) {
        byte innerChunkX = getInnerChunkX(index);
        byte innerChunkY = getInnerChunkY(index);
        int worldX = getWorldPossX(chunkPosX, innerChunkX);
        int worldY = getWorldPossY(chunkPosY, innerChunkY);
        PerlinNoise perlinNoise = mMetaDonnees.get(metaDonnees).perlinNoise;
        final CellRegisterEntry cellRegisterEntry;
        float v = perlinNoise.get(worldX, worldY);
        if (v > 0f && v < 0.5f) {
            cellRegisterEntry = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(RealmTechCoreCell.GRASS_CELL);
        } else if (v >= 0.5f) {
            cellRegisterEntry = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(RealmTechCoreCell.SAND_CELL);
        } else {
            cellRegisterEntry = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(RealmTechCoreCell.WATER_CELL);
        }
        int cellId = world.create();
        world.edit(cellId).create(InfCellComponent.class).set(innerChunkX, innerChunkY, cellRegisterEntry);
        return cellId;
    }

    /**
     * Récupère une position Y dans un chunk via la position Y du monde.
     *
     * @param worldY La position Y dans le monde.
     * @return La position Y dans le chunk.
     */
    public static byte getInnerChunkY(int worldY) {
        return (byte) Math.abs(worldY % WorldMap.CHUNK_SIZE);
    }

    /**
     * Récupère une position X dans un chunk via la position X du monde.
     *
     * @param worldX La position X dans le monde.
     * @return La position X dans le chunk.
     */
    public static byte getInnerChunkX(int worldX) {
        return (byte) Math.abs((worldX % WorldMap.CHUNK_SIZE));
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

    public int getCell(int mapdId, int worldPosX, int worldPosY) {
        int chunk = getChunk(mapdId, worldPosX, worldPosY);
        int ret = -1;
        if (chunk != -1) {
            int[] cells = mChunk.get(chunk).infCellsId;
//            int[] cells = world.getAspectSubscriptionManager().get(Aspect.all(InfCellComponent.class)).getEntities().getData();
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
     * @return chunk id
     */
    public int getChunk(int mapId, int worldPosX, int worldPosY) {
        int ret = -1;
        int chunkX = getChunkX(worldPosX);
        int chunkY = getChunkY(worldPosY);
        int[] chunks = mInfMap.get(mapId).infChunks;
        for (int i = 0; i < chunks.length; i++) {
            InfChunkComponent infChunkComponent = mChunk.get(chunks[i]);
            if (infChunkComponent.chunkPossX == chunkX && infChunkComponent.chunkPossY == chunkY) {
                ret = chunks[i];
                break;
            }
        }
        return ret;
    }

    private void replaceChunk(int mapId, int oldChunk, int newChunkId) {
        int[] chunks = mInfMap.get(mapId).infChunks;
        for (int i = 0; i < chunks.length; i++) {
            if (chunks[i] == oldChunk) {
                chunks[i] = newChunkId;
                return;
            }
        }
    }

    private int[] ajouterChunkAMap(int mapId, int chunkId) {
        int[] infChunks = mInfMap.get(mapId).infChunks;
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
}
