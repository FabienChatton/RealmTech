package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.PlayerSyncPacket;
import ch.realmtech.server.serialize.player.PlayerSerializerConfig;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import java.util.UUID;

@All({PlayerConnexionComponent.class, PositionComponent.class})
public class PlayerSyncSystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    private ComponentMapper<PositionComponent> mPos;
    @Override
    protected void process(int entityId) {
        UUID playerUuid = serverContext.getSystemsAdminServer().getUuidEntityManager().getEntityUuid(entityId);
        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(entityId);
        PositionComponent positionComponent = mPos.get(entityId);
        SerializedApplicationBytes playerEncode = serverContext.getSerializerController().getPlayerSerializerController().encode(new PlayerSerializerConfig().playerId(entityId).writeInventory(false).writeQuestProperty(false));
        int chunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(positionComponent.x));
        int chunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(positionComponent.y));

        serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new PlayerSyncPacket(playerEncode, playerUuid), chunkPosX, chunkPosY);
    }
}
