package ch.realmtech.game.ecs.component;

import ch.realmtech.game.ecs.PoolableComponent;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class CellComponent implements PoolableComponent {
    public TiledMapTileLayer.Cell cell;

    public void init(TextureRegion textureRegion) {
        this.cell = new TiledMapTileLayer.Cell();
        cell.setTile(new StaticTiledMapTile(textureRegion));
    }

    @Override
    public void reset() {
        cell.setTile(null);
        cell = null;
    }
}
