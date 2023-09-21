package ch.realmtechServer.level.map;

import com.badlogic.gdx.maps.tiled.TiledMap;

public class WorldMap extends TiledMap {

    public final static int NUMBER_CHUNK_WITH = 10;
    public final static byte CHUNK_SIZE = 16;
    public final static int WORLD_WITH = NUMBER_CHUNK_WITH * CHUNK_SIZE;
    public final static int NUMBER_CHUNK_HIGH = 10;
    public final static int WORLD_HIGH = NUMBER_CHUNK_HIGH * CHUNK_SIZE;
    public final static byte NUMBER_LAYER = 4;
}
