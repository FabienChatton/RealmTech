package ch.realmtech.game.map;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;

public class RealmTechTiledMap extends TiledMap {
    private final RealmTech context;
    public RealmTechTiledMap(RealmTech context) {
        this.context = context;
    }

    public void creerMapAleatoire() {
        int width = 100;
        int height = 100;
        TextureAtlas groundTitlesAtlas = context.getAssetManager().get("texture/atlas/ground/ground-tiles.atlas", TextureAtlas.class);

        getLayers().add(new TiledMapTileLayer(width, height, 32, 32));
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int randomInt = MathUtils.random(0,groundTitlesAtlas.getRegions().size -1);
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(groundTitlesAtlas.getRegions().get(randomInt)));
                getLayerTiledLayer(0).setCell(i, j, cell);
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
