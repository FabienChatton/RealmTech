package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.packet.clientPacket.ItemOnGroundPacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
@All({ItemComponent.class, PositionComponent.class})
public class ItemOnGroundPosSyncSystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<UuidComponent> mUuid;
    @Override
    protected void process(int entityId) {
        ItemComponent itemComponent = mItem.get(entityId);
        PositionComponent positionComponent = mPos.get(entityId);
        Box2dComponent box2dComponent = mBox2d.get(entityId);
        if (box2dComponent.body.isAwake()) {
            UuidComponent uuidComponent = mUuid.get(entityId);
            serverContext.getServerHandler().broadCastPacket(new ItemOnGroundPacket(uuidComponent.getUuid(), itemComponent.itemRegisterEntry, positionComponent.x, positionComponent.y));
        }
    }
}
