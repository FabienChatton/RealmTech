package ch.realmtech.game.ecs.component;

import ch.realmtech.game.level.cell.CellType;
import com.artemis.PooledComponent;
import com.artemis.annotations.EntityId;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class CellComponent extends PooledComponent {

    @EntityId
    public int parentChunk = -1;
    public byte innerChunkPossX;
    public byte innerChunkPossY;
    public byte layer;
    public CellType cellType;
    public TiledMapTileLayer.Cell cell;
    public StaticTiledMapTile tile;
    @Wire(name = "textureAtlas")
    public TextureAtlas textureAtlas;

    public void init(int parentChunk, byte innerChunkPossX, byte innerChunkPossY, byte layer, CellType cellType) {
        this.parentChunk = parentChunk;
        this.innerChunkPossX = innerChunkPossX;
        this.innerChunkPossY = innerChunkPossY;
        this.cellType = cellType;
        this.layer = layer;
        if (cellType == null) {
            cell = null;
            tile = null;
        } else {
            cell = new TiledMapTileLayer.Cell();
            tile = new StaticTiledMapTile(textureAtlas.findRegion(cellType.textureName));
            cell.setTile(tile);
        }
    }

    @Override
    protected void reset() {
        parentChunk = -1;
        innerChunkPossX = 0;
        innerChunkPossY = 0;
        cellType = null;
    }
}
