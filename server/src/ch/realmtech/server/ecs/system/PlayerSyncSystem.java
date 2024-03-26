package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.PlayerOutOfRange;
import ch.realmtech.server.packet.clientPacket.PlayerSyncPacket;
import ch.realmtech.server.serialize.player.PlayerSerializerConfig;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.ImmutableIntBag;

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
        SerializedApplicationBytes playerEncode = serverContext.getSerializerController().getPlayerSerializerController().encode(new PlayerSerializerConfig().playerId(entityId).writeInventory(false));
        int chunkPosX = MapManager.getChunkPos((int) positionComponent.x);
        int chunkPosY = MapManager.getChunkPos((int) positionComponent.y);

        ImmutableIntBag<?> playersInRange = systemsAdminServer.getPlayerSubscriptionSystem().getPlayersInRangeForChunkPos(new Position(chunkPosX, chunkPosY));
        for (int i = 0; i < playersInRange.size(); i++) {
            int playerId = playersInRange.get(i);
            if (!playerConnexionComponent.playerInRange.contains(playerId)) {
                // new player in range
                playerConnexionComponent.playerInRange.add(playerId);
            }
        }

        for (int i = 0; i < playerConnexionComponent.playerInRange.size(); i++) {
            int playerId = playerConnexionComponent.playerInRange.get(i);
            if (!playersInRange.contains(playerId)) {
                // remove player in range
                playerConnexionComponent.playerInRange.removeValue(playerId);
                serverContext.getServerConnexion().sendPacketTo(new PlayerOutOfRange(playerUuid), playerConnexionComponent.channel);
            }
        }

        serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new PlayerSyncPacket(playerEncode, playerUuid), chunkPosX, chunkPosY);
    }
}
