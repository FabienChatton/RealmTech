package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.ChunkComponent;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.game.level.map.RealmTechTiledMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkAdapter;
import com.artemis.utils.IntBag;

public class ChunkManager extends EntityLinkManager {
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
        for (byte chunkPossX = 0; chunkPossX < RealmTechTiledMap.NUMBER_CHUNK_WITH; chunkPossX++) {
            for (byte chunkPossY = 0; chunkPossY < RealmTechTiledMap.NUMBER_CHUNK_HIGH; chunkPossY++) {
                int newChunk = world.create();
                world.edit(newChunk).create(ChunkComponent.class).init(saveId, chunkPossX, chunkPossY);
                world.getSystem(CellManager.class).genereteNewCells(newChunk, perlinNoise);
            }
        }
        inserted(chunks);
    }
}
