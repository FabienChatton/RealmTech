package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.CellComponent;
import ch.realmtech.game.ecs.component.ChunkComponent;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkAdapter;
import com.artemis.utils.IntBag;

public class CellManager extends EntityLinkManager {
    private ComponentMapper<ChunkComponent> mChunk;
    private ComponentMapper<CellComponent> mCell;

    public void register() {
        register(CellComponent.class, new LinkAdapter() {
            @Override
            public void onLinkEstablished(int sourceId, int targetId) {
                //System.out.println("onLinkEstablished");
                ChunkComponent chunkComponent = mChunk.get(targetId);
                CellComponent cellComponent = mCell.get(sourceId);
                world.getSystem(WorldMapManager.class).placeOnMap(
                        getWorldPossX(chunkComponent.chunkPossX, cellComponent.innerChunkPossX),
                        getWorldPossY(chunkComponent.chunkPossY, cellComponent.innerChunkPossY),
                        cellComponent.layer,
                        cellComponent.cell
                );
            }

            @Override
            public void onLinkKilled(int sourceId, int targetId) {
                world.delete(sourceId);
            }

            @Override
            public void onTargetDead(int sourceId, int deadTargetId) {
                world.delete(sourceId);
            }

            @Override
            public void onTargetChanged(int sourceId, int targetId, int oldTargetId) {
                System.out.println("onTargetChanged");
                System.out.println("s " + sourceId + " t " + targetId + " old " + oldTargetId);
            }
        });
    }

    public void generateNewCells(int chunkId, PerlinNoise perlinNoise) {
        final ChunkComponent parentChunkComponent = mChunk.get(chunkId);
        IntBag cells = new IntBag(WorldMap.CHUNK_SIZE * WorldMap.CHUNK_SIZE);
        for (byte innerChunkX = 0; innerChunkX < WorldMap.CHUNK_SIZE; innerChunkX++) {
            for (byte innerChunkY = 0; innerChunkY < WorldMap.CHUNK_SIZE; innerChunkY++) {
                int worldX = getWorldPossX(parentChunkComponent.chunkPossX, innerChunkX);
                int worldY = getWorldPossY(parentChunkComponent.chunkPossY, innerChunkY);
                int cell = world.create();
                CellType cellType;
                if (perlinNoise.getGrid()[worldX][worldY] > 0f && perlinNoise.getGrid()[worldX][worldY] < 0.5f) {
                    cellType = CellType.GRASS;
                } else if (perlinNoise.getGrid()[worldX][worldY] >= 0.5f) {
                    cellType = CellType.SAND;
                } else {
                    cellType = CellType.WATER;
                }
                CellComponent cellComponent = world.edit(cell).create(CellComponent.class);
                world.inject(cellComponent);
                cellComponent.init(chunkId, innerChunkX, innerChunkY, (byte) 0, cellType);
                cells.add(cell);
            }
        }
        inserted(cells);
    }

    public int getWorldPossX(int chunkPossX, int innerChunkX) {
        return chunkPossX * WorldMap.CHUNK_SIZE + innerChunkX;
    }

    public int getWorldPossY(int chunkPossY, int innerChunkY) {
        return chunkPossY * WorldMap.CHUNK_SIZE + innerChunkY;
    }

    public int getCell(int worldX, int worldY, byte layer) {
        int chunkId = world.getSystem(ChunkManager.class).getChunk(worldX, worldY);
        for (int cellId : getCells(chunkId).getData()) {
            byte innerX = (byte) (worldX % WorldMap.CHUNK_SIZE);
            byte innerY = (byte) (worldY % WorldMap.CHUNK_SIZE);
            CellComponent cellComponent = mCell.create(cellId);
            if (cellComponent.innerChunkPossX == innerX &&
                    cellComponent.innerChunkPossY == innerY &&
                    cellComponent.layer == layer) {
                return cellId;
            }
        }
        return -1;
    }

    /**
     * Donne toutes les cellules qui appartiennent un chunk
     *
     * @param partentChunkId le chunk id du parent
     * @return un bag qui contient toutes les cellules du chunk partent passé en parameter
     */
    public IntBag getCells(int partentChunkId) {
        IntBag ret = new IntBag();
        IntBag cells = world.getAspectSubscriptionManager().get(Aspect.all(CellComponent.class)).getEntities();
        for (int cell : cells.getData()) {
            CellComponent cellComponent = mCell.create(cell);
            if (cellComponent.parentChunk == partentChunkId) {
                ret.add(cell);
            }
        }
        return ret;
    }

    public byte getInnerChunkPossY(byte innerChunkPoss) {
        return (byte) (innerChunkPoss & 0x0F);
    }

    public byte getInnerChunkPossX(byte innerChunkPoss) {
        return (byte) ((innerChunkPoss >> 4) & 0x0F);
    }

    public byte getInnerChunkPoss(byte innerChunkPossX, byte innerChunkPossY) {
        return (byte) ((innerChunkPossX << 4) + innerChunkPossY);
    }
}
