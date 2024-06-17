package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.divers.Position;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.ItemOnGroundPacket;
import ch.realmtech.server.packet.clientPacket.ItemOnGroundSupprimerPacket;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.ImmutableIntBag;
import com.artemis.utils.IntBag;

import java.util.UUID;

@All({ItemComponent.class, PositionComponent.class})
public class ItemOnGroundPosSyncSystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;

    @Override
    protected void process(int entityId) {
        ItemComponent itemComponent = mItem.get(entityId);
        PositionComponent itemPos = mPos.get(entityId);
        Box2dComponent box2dComponent = mBox2d.get(entityId);

        IntBag players = systemsAdminServer.getPlayerManagerServer().getPlayers();
        for (int i = 0; i < players.size(); i++) {
            int playerId = players.get(i);
            PositionComponent playerPos = mPos.get(playerId);
            int playerChunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(playerPos.x));
            int playerChunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(playerPos.y));
            PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
            ImmutableIntBag<?> entityInRangeForChunkPos = systemsAdminServer.getPlayerSubscriptionSystem().getEntityInRangeForChunkPos(new Position(playerChunkPosX, playerChunkPosY), Aspect.all(MobComponent.class, PositionComponent.class));

            if (!playerConnexionComponent.itemInRange.contains(entityId)) {
                if (entityInRangeForChunkPos.contains(entityId)) {
                    playerConnexionComponent.itemInRange.add(entityId);
                }
            } else {
                if (!entityInRangeForChunkPos.contains(entityId)) {
                    playerConnexionComponent.itemInRange.removeValue(entityId);
                    UUID entityUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId);
                    serverContext.getServerConnexion().sendPacketTo(new ItemOnGroundSupprimerPacket(entityUuid), playerConnexionComponent.channel);
                }
            }
        }

        if (box2dComponent.body.isAwake()) {
            UUID uuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(entityId);
            int itemChunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(itemPos.x));
            int itemChunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(itemPos.y));
            serverContext.getServerConnexion().sendPacketToSubscriberForChunkPos(new ItemOnGroundPacket(uuid, itemComponent.itemRegisterEntry.getId(), itemPos.x, itemPos.y), itemChunkPosX, itemChunkPosY);
        }
    }
}
