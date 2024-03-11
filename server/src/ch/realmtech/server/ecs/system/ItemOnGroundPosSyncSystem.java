package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.ItemOnGroundPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

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
    @Override
    protected void process(int entityId) {
        ItemComponent itemComponent = mItem.get(entityId);
        PositionComponent positionComponent = mPos.get(entityId);
        Box2dComponent box2dComponent = mBox2d.get(entityId);
        if (box2dComponent.body.isAwake()) {
            UUID uuid = systemsAdminServer.uuidEntityManager.getEntityUuid(entityId);
            serverContext.getServerConnexion().broadCastPacket(new ItemOnGroundPacket(uuid, itemComponent.itemRegisterEntry.getId(), positionComponent.x, positionComponent.y));
        }
    }
}
