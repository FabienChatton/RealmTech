package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
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
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;
    @Override
    protected void process(int entityId) {
        UUID playerUuid = serverContext.getSystemsAdmin().uuidEntityManager.getEntityUuid(entityId);
        SerializedApplicationBytes playerEncode = serverContext.getSerializerController().getPlayerSerializerController().encode(new PlayerSerializerConfig().playerId(entityId).writeInventory(false));
        serverContext.getServerHandler().broadCastPacket(new PlayerSyncPacket(playerEncode, playerUuid));
    }
}
