package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.ChunkComponent;
import ch.realmtech.game.ecs.component.CellComponent;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
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

    public void genereteNewCells(int chunkId, PerlinNoise perlinNoise) {
        final ChunkComponent parentChunkComponent = mChunk.get(chunkId);
        IntBag cells = new IntBag(GameChunk.CHUNK_SIZE * GameChunk.CHUNK_SIZE);
        for (byte innerChunkX = 0; innerChunkX < GameChunk.CHUNK_SIZE; innerChunkX++) {
            for (byte innerChunkY = 0; innerChunkY < GameChunk.CHUNK_SIZE; innerChunkY++) {
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
        return chunkPossX * GameChunk.CHUNK_SIZE + innerChunkX;
    }

    public int getWorldPossY(int chunkPossY, int innerChunkY) {
        return chunkPossY * GameChunk.CHUNK_SIZE + innerChunkY;
    }
}
