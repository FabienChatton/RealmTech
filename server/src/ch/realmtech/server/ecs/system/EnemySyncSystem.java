package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.packet.clientPacket.EnemySetPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

@All({PositionComponent.class, EnemyComponent.class})
public class EnemySyncSystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private SystemsAdminServer systemsAdminServer;

    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;

    @Override
    protected void process(int entityId) {
        SerializedApplicationBytes enemyData = serverContext.getSerializerController().getEnemySerializerController().encode(entityId);
        PositionComponent positionComponent = mPos.get(entityId);
        int playerChunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(positionComponent.x));
        int playerChunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(positionComponent.y));

        serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new EnemySetPacket(enemyData), playerChunkPosX, playerChunkPosY);
    }
}
