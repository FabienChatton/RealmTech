package ch.realmtech.server.enemy;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.packet.clientPacket.EnemyDeletePacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;

import java.util.UUID;

@All(EnemyComponent.class)
public class EnemySystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<EnemyComponent> mEnemy;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<LifeComponent> mLife;

    @Wire(name = "physicWorld")
    private World physicWorld;
    @Override
    protected void process(int entityId) {
        EnemyComponent enemyComponent = mEnemy.get(entityId);
        enemyComponent.getIaTestSteerable().update(world.getDelta());

        LifeComponent lifeComponent = mLife.get(entityId);
        if (lifeComponent.getHeart() <= 0) {
            destroyEnemyServer(entityId);
        }
    }

    /**
     * Destroy enemy on server and send to all client in range.
     * The enemy must not been deleted from the world before this call.
     *
     * @param enemyId The enemy to delete.
     */
    public void destroyEnemyServer(int enemyId) {
        PositionComponent enemyPos = mPos.get(enemyId);
        UUID entityUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(enemyId);
        int worldPosX = MapManager.getWorldPos(enemyPos.x);
        int worldPosY = MapManager.getWorldPos(enemyPos.y);
        int chunkPosX = MapManager.getChunkPos(worldPosX);
        int chunkPosY = MapManager.getChunkPos(worldPosY);

        serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new EnemyDeletePacket(entityUuid), chunkPosX, chunkPosY);
        systemsAdminServer.getMobManager().destroyWorldEnemy(enemyId);
    }
}
