package ch.realmtech.game.level.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class WorldMap extends TiledMap {

    public TiledMapTileLayer getLayerTiledLayer(int index) {
        return (TiledMapTileLayer) getLayers().get(index);
    }
}
