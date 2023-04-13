package ch.realmtech.game.ecs.component;

import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
public class CellComponent extends PooledComponent {

    @EntityId
    public int parentChunk = -1;
    public byte innerChunkPossX;
    public byte innerChunkPossY;
    public byte layer;
    public TiledMapTileLayer.Cell cell;
    public StaticTiledMapTile tile;
    public CellRegisterEntry cellRegisterEntry;

    public void set(int parentChunk, byte innerChunkPossX, byte innerChunkPossY, byte layer, CellRegisterEntry cellRegisterEntry) {
        this.parentChunk = parentChunk;
        this.innerChunkPossX = innerChunkPossX;
        this.innerChunkPossY = innerChunkPossY;
        this.layer = layer;
        this.cellRegisterEntry = cellRegisterEntry;
        if (cell != null && tile != null){
            tile.setTextureRegion(cellRegisterEntry.getTextureRegion());
        } else {
            cell = new TiledMapTileLayer.Cell();
            tile = new StaticTiledMapTile(cellRegisterEntry.getTextureRegion());
            cell.setTile(tile);
        }
    }

    @Override
    protected void reset() {
        parentChunk = -1;
        innerChunkPossX = 0;
        innerChunkPossY = 0;
    }
}
