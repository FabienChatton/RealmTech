package ch.realmtech.game.ecs.component;

import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.PooledComponent;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import static com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;

public class InfCellComponent extends PooledComponent {
    public byte innerPosX;
    public byte innerPosY;
    public Cell cell;
    public StaticTiledMapTile tile;
    public CellRegisterEntry cellRegisterEntry;

    public InfCellComponent set (byte innerPosX, byte innerPosY, CellRegisterEntry cellRegisterEntry) {
        this.innerPosX = innerPosX;
        this.innerPosY = innerPosY;
        this.cellRegisterEntry = cellRegisterEntry;
        if (cell != null && tile != null){
            tile.setTextureRegion(cellRegisterEntry.getTextureRegion());
        } else {
            cell = new Cell();
            tile = new StaticTiledMapTile(cellRegisterEntry.getTextureRegion());
            cell.setTile(tile);
        }
        return this;
    }
    @Override
    protected void reset() {
        innerPosX = 0;
        innerPosY = 0;
    }
}
