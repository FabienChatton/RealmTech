package ch.realmtech.game.ecs.component;

import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.PooledComponent;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class InfCellComponent extends PooledComponent {
    public byte posX;
    public byte posY;
    public TiledMapTileLayer.Cell cell;
    public StaticTiledMapTile tile;
    public CellRegisterEntry cellRegisterEntry;

    public InfCellComponent set (byte innerPosX, byte innerPosY, CellRegisterEntry cellRegisterEntry) {
        this.posX = innerPosX;
        this.posY = innerPosY;
        this.cellRegisterEntry = cellRegisterEntry;
        if (cell != null && tile != null){
            tile.setTextureRegion(cellRegisterEntry.getTextureRegion());
        } else {
            cell = new TiledMapTileLayer.Cell();
            tile = new StaticTiledMapTile(cellRegisterEntry.getTextureRegion());
            cell.setTile(tile);
        }
        return this;
    }
    @Override
    protected void reset() {
        posX = 0;
        posY = 0;
    }
}
