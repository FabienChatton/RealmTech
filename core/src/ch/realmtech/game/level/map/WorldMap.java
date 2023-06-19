package ch.realmtech.game.level.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class WorldMap extends TiledMap {

    public final static int NUMBER_CHUNK_WITH = 10;
    public final static byte CHUNK_SIZE = 16;
    public final static int WORLD_WITH = NUMBER_CHUNK_WITH * CHUNK_SIZE;
    public final static int NUMBER_CHUNK_HIGH = 10;
    public final static int WORLD_HIGH = NUMBER_CHUNK_HIGH * CHUNK_SIZE;
    public final static byte NUMBER_LAYER = 4;

    public TiledMapTileLayer getLayerTiledLayer(byte layer) {
        if (layer > 3) throw new IllegalArgumentException("Il existe uniquement 4 layer. 0, 1, 2, 3");
        for (int i = 0; i <= layer; i++) {
            if (getLayers().size() == i) {
                getLayers().add(new TiledMapTileLayer(WORLD_WITH, WORLD_HIGH, 32, 32));
            }
        }
        return (TiledMapTileLayer) getLayers().get(layer);
    }
}
