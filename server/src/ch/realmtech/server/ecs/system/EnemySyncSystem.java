package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.MobComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.enemy.EnemyComponent;
import ch.realmtech.server.packet.clientPacket.EnemyDeletePacket;
import ch.realmtech.server.packet.clientPacket.EnemySetPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.ImmutableIntBag;
import com.artemis.utils.IntBag;

import java.util.UUID;

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

        IntBag players = systemsAdminServer.getPlayerManagerServer().getPlayers();
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
            ImmutableIntBag<?> entityInRangeForChunkPos = systemsAdminServer.getPlayerSubscriptionSystem().getEntityInRangeForChunkPos(new Position(playerChunkPosX, playerChunkPosY), Aspect.all(MobComponent.class, PositionComponent.class));

            if (!playerConnexionComponent.mobInRange.contains(entityId)) {
                if (entityInRangeForChunkPos.contains(entityId)) {
                    playerConnexionComponent.mobInRange.add(entityId);
                }
            } else {
                if (!entityInRangeForChunkPos.contains(entityId)) {
                    playerConnexionComponent.mobInRange.removeValue(entityId);
                    UUID entityUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId);
                    serverContext.getServerConnexion().sendPacketTo(new EnemyDeletePacket(entityUuid), playerConnexionComponent.channel);
                }
            }
        }
        serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new EnemySetPacket(enemyData), playerChunkPosX, playerChunkPosY);
    }
}
