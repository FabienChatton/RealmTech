package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import ch.realmtech.game.mod.RealmTechCoreCell;
import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

public class WorldMapManager extends Manager {

    @Wire(name = "context")
    RealmTech context;
    @Deprecated
    int worldMapId;

    private ComponentMapper<WorldMapComponent> mWorldMap;
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfMetaDonneesComponent> mMetaDonnees;
    private ComponentMapper<InfChunkComponent> mChunk;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<InfLayerComponent> mLayer;

    public static int getWorldPossX(int chunkPossX, int innerChunkX) {
        return chunkPossX * WorldMap.CHUNK_SIZE + innerChunkX;
    }

    public static int getWorldPossY(int chunkPossY, int innerChunkY) {
        return chunkPossY * WorldMap.CHUNK_SIZE + innerChunkY;
    }


    public void init(int worldMapId, int worldWith, int worldHigh, int tileWith, int tileHigh, byte numberLayer) {
        this.worldMapId = worldMapId;
        initMap(worldWith, worldHigh, tileWith, tileHigh, numberLayer);
    }

    public void initMap(int worldWith, int worldHigh, int tileWith, int tileHigh, byte numberLayer) {
        final WorldMapComponent worldMap = mWorldMap.get(worldMapId);
        worldMap.worldMap = new WorldMap();
        for (int i = 0; i < numberLayer; i++) {
            worldMap.worldMap.getLayers().add(new TiledMapTileLayer(worldWith, worldHigh, tileWith, tileHigh));
        }
        worldMap.worldMap.getProperties().put("spawn-point", new Vector2(worldWith / 2, worldHigh / 2));
    }

    /**
     * @param metaDonnees Les méta données du monde
     * @param chunkPosX   La position X du chunk
     * @param chunkPosY   La position Y du chunk
     * @return l'id du nouveau chunk
     */
    public int generateNewChunk(int metaDonnees, int chunkPosX, int chunkPosY) {
        int chunkId = world.create();
        int[] layerId = new int[WorldMap.NUMBER_LAYER];
        for (byte i = 0; i < layerId.length; i++) {
            layerId[i] = world.create();
            int[] cellsId = new int[WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE];
            for (short j = 0; j < cellsId.length; j++) {
                cellsId[j] = generateNewCell(metaDonnees, chunkPosX, chunkPosY, j);
            }
            world.edit(layerId[i]).create(InfLayerComponent.class).set(i, cellsId);
        }
        world.edit(chunkId).create(InfChunkComponent.class).set(chunkPosX, chunkPosY, layerId);
        return chunkId;
    }

    private int generateNewCell(int metaDonnees, int chunkPosX, int chunkPosY, short index) {
        byte innerChunkX = getInnerChunkX(index);
        byte innerChunkY = getInnerChunkY(index);
        int worldX = getWorldPossX(chunkPosX, innerChunkX);
        int worldY = getWorldPossY(chunkPosY, innerChunkY);
        PerlinNoise perlinNoise = mMetaDonnees.get(metaDonnees).perlinNoise;
        final CellRegisterEntry cellRegisterEntry;
        if (perlinNoise.getGrid()[worldX][worldY] > 0f && perlinNoise.getGrid()[worldX][worldY] < 0.5f) {
            cellRegisterEntry = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(RealmTechCoreCell.GRASS_CELL);
        } else if (perlinNoise.getGrid()[worldX][worldY] >= 0.5f) {
            cellRegisterEntry = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(RealmTechCoreCell.SAND_CELL);
        } else {
            cellRegisterEntry = RealmTechCoreMod.REALM_TECH_CORE_CELL_REGISTRY.get(RealmTechCoreCell.WATER_CELL);
        }
        int cellId = world.create();
        world.edit(cellId).create(InfCellComponent.class).set(innerChunkX, innerChunkY, cellRegisterEntry);
        return cellId;
    }

    public void placeWorldMap(int worldMapId) {
        world.getSystem(WorldMapRendererSystem.class).setMapRenderer(mWorldMap.get(worldMapId).worldMap);
    }

    public void placeWorldInfMap(int infWorldId) {
        world.getSystem(WorldMapRendererSystem.class).setMapRenderer(mInfMap.get(infWorldId).worldMap);
    }

    @Deprecated
    public void placeOnMap(int worldPossX, int worldPossY, byte layer, TiledMapTileLayer.Cell cell) {
        ((TiledMapTileLayer) mWorldMap.get(worldMapId).worldMap.getLayers().get(layer)).setCell(worldPossX, worldPossY, cell);
    }

    public void mountInfMap(int worldId) {
        InfMapComponent infMapComponent = mInfMap.get(worldId);
        int[] infChunks = infMapComponent.infChunks;
        for (int i = 0; i < infChunks.length; i++) {
            mountChunk(worldId, infChunks[i]);
        }
    }

    public void mountChunk(int worldMapId, int infChunkId) {
        InfChunkComponent infChunkComponent = mChunk.get(infChunkId);
        placeOnMapInf(worldMapId, infChunkId);
//        for (int i = 0; i < infChunkComponent.infLayers.length; i++) {
//            InfLayerComponent infLayerComponent = mLayer.get(infChunkComponent.infLayers[i]);
//            for (int j = 0; j < infLayerComponent.infCells.length; j++) {
//                InfCellComponent infCellComponent = mCell.get(infLayerComponent.infCells[j]);
//                int worldPosX = getWorldPossX(infChunkComponent.chunkPossX, infCellComponent.posX);
//                int worldPosY = getWorldPossY(infChunkComponent.chunkPossY, infCellComponent.posY);
//                placeOnMap(worldPosX, worldPosY, infLayerComponent.layer, infCellComponent.cell);
//            }
//        }
    }

    public void placeOnMapInf(int infMapId, int chunkId) {
        InfMapComponent infMapComponent = mInfMap.get(infMapId);
        InfChunkComponent infChunkComponent = mChunk.get(chunkId);
        for (byte i = 0; i < infChunkComponent.infLayers.length; i++) {
            InfLayerComponent infLayerComponent = mLayer.get(infChunkComponent.infLayers[i]);
            for (short j = 0; j < infLayerComponent.infCells.length; j++) {
                InfCellComponent infCellComponent = mCell.get(infLayerComponent.infCells[j]);
                infMapComponent.worldMap.getLayerTiledLayer(infLayerComponent.layer)
                        .setCell(getWorldPossX(infChunkComponent.chunkPossX, infCellComponent.posX),
                                getWorldPossY(infChunkComponent.chunkPossY, infCellComponent.posY),
                                infCellComponent.cell
                        );
            }

        }
    }

    /**
     * Récupère une position Y dans un chunk via la position Y du monde.
     *
     * @param worldY La position Y dans le monde.
     * @return La position Y dans le chunk.
     */
    public static byte getInnerChunkY(int worldY) {
        return (byte) (worldY % WorldMap.CHUNK_SIZE);
    }

    /**
     * Récupère une position X dans un chunk via la position X du monde.
     *
     * @param worldX La position X dans le monde.
     * @return La position X dans le chunk.
     */
    public static byte getInnerChunkX(int worldX) {
        return (byte) (worldX % WorldMap.CHUNK_SIZE);
    }

    /**
     * @param index l'index d'un tableau
     * @return la position x dans le chunk
     */
    public static byte getInnerChunkX(short index) {
        return (byte) (index % WorldMap.CHUNK_SIZE);
    }

    /**
     * @param index l'index d'un tableau
     * @return la position y dans le chunk
     */

    public static byte getInnerChunkY(short index) {
        return (byte) (index / WorldMap.CHUNK_SIZE);
    }
}
