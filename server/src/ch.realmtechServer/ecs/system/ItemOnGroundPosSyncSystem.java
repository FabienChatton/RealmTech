package ch.realmtechServer.ecs.system;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.component.PositionComponent;
import ch.realmtechServer.packet.clientPacket.ItemOnGroundPacket;
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
    @Override
    protected void process(int entityId) {
        ItemComponent itemComponent = mItem.get(entityId);
        PositionComponent positionComponent = mPos.get(entityId);
        serverContext.getServerHandler().broadCastPacket(new ItemOnGroundPacket(itemComponent.uuid, itemComponent.itemRegisterEntry, positionComponent.x, positionComponent.y));
    }
}