package ch.realmtechServer.ecs.system;

import ch.realmtechServer.divers.Position;
import ch.realmtechServer.ecs.component.InfChunkComponent;
import ch.realmtechServer.ecs.component.InfMapComponent;
import ch.realmtechServer.ecs.component.PlayerConnexionComponent;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.managers.TagManager;

import java.util.List;

public class MapUnloadUnusedChunkSystem extends BaseSystem {
    private ComponentMapper<InfMapComponent> mInfMap;
    private ComponentMapper<InfChunkComponent> mChunk;
    @Override
    protected void processSystem() {
        InfMapComponent infMapComponent = mInfMap.get(world.getSystem(TagManager.class).getEntityId("infMap"));
        List<PlayerConnexionComponent> playersConnexion = world.getSystem(PlayerManagerServer.class).getPlayersConnexion();
        for (int i = 0; i < infMapComponent.infChunks.length; i++) {
            if (!mChunk.has(infMapComponent.infChunks[i])) continue;
            InfChunkComponent infChunkComponent = mChunk.get(infMapComponent.infChunks[i]);
            for (PlayerConnexionComponent playerConnexionComponent : playersConnexion) {
                for (Position chunkPos : playerConnexionComponent.chunkPoss) {
                    if (world.getSystem(MapSystemServer.class).chunkEstDansLaRenderDistance(chunkPos, infChunkComponent.chunkPosX, infChunkComponent.chunkPosY)) {
                        infMapComponent.infChunks = world.getSystem(MapSystemServer.class).damneChunkServer(infMapComponent.infChunks, infMapComponent.infChunks[i], infMapComponent.getMetaDonnesComponent(world));
                    }
                }
            }
        }
    }
}
