package ch.realmtech.game.level.cell;

import ch.realmtech.game.level.chunk.GameChunk;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

public class GameCell {
    private final CellType cellType;
    private final GameChunk gameChunk;
    private final byte innerChunkPoss;
    private final TiledMapTileLayer.Cell cell;
    private final StaticTiledMapTile tile;
    public GameCell(GameChunk gameChunk, byte innerChunkPossX, byte innerChunkPossY, CellType cellType) {
        this.gameChunk = gameChunk;
        this.cellType = cellType;
        innerChunkPoss = (byte) ((innerChunkPossX << 4) + innerChunkPossY);
        cell = new TiledMapTileLayer.Cell();
        tile = new StaticTiledMapTile(gameChunk.getContext().getTextureAtlas().findRegion(cellType.textureName));
        cell.setTile(tile);
    }

    public CellType getCellType() {
        return cellType;
    }

    public void placeCellOnMap(int layer) {
        gameChunk.getMap().getLayerTiledLayer(layer).setCell(gameChunk.getWorldPossX(getInnerChunkPossX()), gameChunk.getWorldPossY(getInnerChunkPossY()),cell);
    }

    public byte getInnerChunkPossY(){
        return (byte) (innerChunkPoss & 0x0F);
    }

    public byte getInnerChunkPossX() {
        return (byte) ((innerChunkPoss >> 4) & 0x0F);
    }
}
