package ch.realmtech.game.map;

import ch.realmtech.RealmTech;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.worldGeneration.PerlinNoise;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class RealmTechTiledMap extends TiledMap {
    public final static int NUMBER_CHUNK_WITH = 10;
    public final static int NUMBER_CHUNK_HIGH = 10;

    public final static int WORLD_WITH = NUMBER_CHUNK_WITH * GameChunk.CHUNK_SIZE;
    public final static int WORLD_HIGH = NUMBER_CHUNK_HIGH * GameChunk.CHUNK_SIZE;
    private final RealmTech context;
    private PerlinNoise perlinNoise;
    public RealmTechTiledMap(RealmTech context) {
        this.context = context;
//        perlinNoise = new PerlinNoise(new Random(), 1, MAP_WIDTH, MAP_HEIGHT);
//        perlinNoise.initialise();
    }

    public void creerMapAleatoire() {
        int width = 100;
        int height = 100;
        PerlinNoise perlinNoise = new PerlinNoise(new Random(666), 1, width, height);
        perlinNoise.initialise();
        getProperties().put("spawn-point", new Vector2(width / 2, height/2));
        context.getEcsEngine().generateBodyWorldBorder(0,0,width,width);
        getLayers().add(new TiledMapTileLayer(width, height, 32, 32));

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                final CellType cellType;
                if (perlinNoise.getGrid()[i][j] > 0f && perlinNoise.getGrid()[i][j] < 0.5f){
                    cellType = CellType.GRASS;
                } else if (perlinNoise.getGrid()[i][j] >= 0.5f) {
                    cellType = CellType.SAND;
                } else {
                    cellType = CellType.WATER;
                }
                getLayerTiledLayer(0).setCell(i, j, new TiledMapTileLayer.Cell().setTile(new StaticTiledMapTile(context.getTextureAtlas().findRegion(cellType.textureName))));
            }
        }
    }


    public TiledMapTileLayer getLayerTiledLayer(int index) {
        return (TiledMapTileLayer) super.getLayers().get(index);
    }

    public TiledMapTileLayer getLayerTiledLayer(String name) {
        return (TiledMapTileLayer) super.getLayers().get(name);
    }
}
