package ch.realmtech.game.level.cell;

import ch.realmtech.game.level.chunk.GameChunk;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class GameCell {
    private final CellType cellType;
    private final GameChunk gameChunk;
    private final byte innerChunkPossX;
    private final byte innerChunkPossY;
    private final TiledMapTileLayer.Cell cell;
    private final StaticTiledMapTile tile;
    public GameCell(GameChunk gameChunk, byte innerChunkPossX, byte innerChunkPossY, CellType cellType) {
        this.gameChunk = gameChunk;
        this.cellType = cellType;
        this.innerChunkPossX = innerChunkPossX;
        this.innerChunkPossY = innerChunkPossY;
        cell = new TiledMapTileLayer.Cell();
        tile = new StaticTiledMapTile(gameChunk.getContext().getTextureAtlas().findRegion(cellType.textureName));
        cell.setTile(tile);
    }

    public CellType getCellType() {
        return cellType;
    }

    public void placeCellOnMap(int layer) {
        gameChunk.getMap().getLayerTiledLayer(layer).setCell(gameChunk.getWorldPossX(innerChunkPossX), gameChunk.getWorldPossY(innerChunkPossY),cell);
    }
}
