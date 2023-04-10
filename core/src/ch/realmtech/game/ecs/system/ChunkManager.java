package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.ChunkComponent;
import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkAdapter;
import com.artemis.utils.IntBag;

public class ChunkManager extends EntityLinkManager {
    private ComponentMapper<ChunkComponent> mChunk;
    public void register(){
        register(ChunkComponent.class, new LinkAdapter() {
            @Override
            public void onLinkEstablished(int sourceId, int targetId) {
                //System.out.println("onLinkEstablished");
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

    public void genereteNewChunks(int saveId, PerlinNoise perlinNoise) {
        IntBag chunks = new IntBag();
        for (byte chunkPossX = 0; chunkPossX < WorldMap.NUMBER_CHUNK_WITH; chunkPossX++) {
            for (byte chunkPossY = 0; chunkPossY < WorldMap.NUMBER_CHUNK_HIGH; chunkPossY++) {
                int newChunk = world.create();
                world.edit(newChunk).create(ChunkComponent.class).init(saveId, chunkPossX, chunkPossY);
                world.getSystem(CellManager.class).generateNewCells(newChunk, perlinNoise);
            }
        }
        inserted(chunks);
    }

    public int getChunk(int worldX, int worldY) {
        int ret = -1;
        int chunkPossX = worldX / WorldMap.CHUNK_SIZE;
        int chunkPossY = worldY / WorldMap.CHUNK_SIZE;
        IntBag chunksId = world.getAspectSubscriptionManager().get(Aspect.all(ChunkComponent.class)).getEntities();
        for (int chunkId : chunksId.getData()) {
            ChunkComponent chunkComponent = mChunk.get(chunkId);
            if (chunkComponent.chunkPossX == chunkPossX && chunkComponent.chunkPossY == chunkPossY) {
                ret = chunkId;
                break;
            }
        }
        return ret;
    }
}
