package ch.realmtech.game.ecs.component;

import ch.realmtech.game.ecs.PoolableComponent;
import ch.realmtech.game.level.cell.CellType;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.util.EnumMap;

public class TiledMapComponent implements PoolableComponent {
    public EnumMap<CellType, StaticTiledMapTile> cellTypeCash;

    public TiledMapComponent() {
        cellTypeCash = new EnumMap<>(CellType.class);
    }

    public void init(TextureAtlas textureAtlas, CellType cellType){
        if (cellTypeCash.get(cellType) == null) {
            cellTypeCash.put(cellType, new StaticTiledMapTile(textureAtlas.findRegion(cellType.textureName)));
        }
    }
    @Override
    public void reset() {

    }
}
