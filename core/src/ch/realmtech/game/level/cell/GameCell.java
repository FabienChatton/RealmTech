package ch.realmtech.game.level.cell;

import ch.realmtech.game.io.Save;
import ch.realmtech.game.level.chunk.GameChunk;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

import java.io.IOException;

public class GameCell {
    private final CellType cellType;
    private final GameChunk gameChunk;
    private final byte innerChunkPoss;
    private final TiledMapTileLayer.Cell cell;
    private final StaticTiledMapTile tile;

    public GameCell(GameChunk gameChunk, byte innerChunkPoss, CellType cellType) {
        this.gameChunk = gameChunk;
        this.cellType = cellType;
        this.innerChunkPoss = innerChunkPoss;
        if (cellType == null) {
            cell = null;
            tile = null;
        } else {
            cell = new TiledMapTileLayer.Cell();
            tile = new StaticTiledMapTile(gameChunk.getMap().getContext().getTextureAtlas().findRegion(cellType.textureName));
            cell.setTile(tile);
        }
    }

    public GameCell(GameChunk gameChunk, byte innerChunkPossX, byte innerChunkPossY, CellType cellType) {
        this(gameChunk,GameCell.getInnerChunkPoss(innerChunkPossX,innerChunkPossY), cellType);
    }

    public CellType getCellType() {
        return cellType;
    }

    public void placeCellOnMap(int layer) {
        gameChunk.getMap().getLayerTiledLayer(layer).setCell(gameChunk.getWorldPossX(getInnerChunkPossX()), gameChunk.getWorldPossY(getInnerChunkPossY()),cell);
    }

    public byte getInnerChunkPossY(){
        return getInnerChunkPossY(innerChunkPoss);
    }

    public static byte getInnerChunkPossY(byte innerChunkPoss) {
        return (byte) (innerChunkPoss & 0x0F);
    }

    public byte getInnerChunkPossX() {
        return getInnerChunkPossX(innerChunkPoss);
    }

    public static byte getInnerChunkPossX(byte innerChunkPoss) {
        return (byte) ((innerChunkPoss >> 4) & 0x0F);
    }

    public void write(Save save) throws IOException {
        save.write(CellType.getIdCellType(cellType));
        save.write(innerChunkPoss);
    }

    public static byte getInnerChunkPoss(byte innerChunkPossX, byte innerChunkPossY) {
        return (byte) ((innerChunkPossX << 4) + innerChunkPossY);
    }
}
