package ch.realmtech.server.item;

import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import com.artemis.ComponentMapper;
import com.artemis.World;

import java.util.UUID;

public class ItemInfoHelper {
    public static String dumpItem(World world, int itemId) {
        SystemsAdminCommun systemsAdminCommun = world.getRegistered("systemsAdmin");
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
        ComponentMapper<PositionComponent> mPos = world.getMapper(PositionComponent.class);
        ItemComponent itemComponent = mItem.get(itemId);
        UUID uuid = systemsAdminCommun.uuidEntityManager.getEntityUuid(itemId);

        if (mPos.has(itemId)) {
            PositionComponent positionComponent = mPos.get(itemId);
            return String.format("%d, %s, %s, %s", itemId, itemComponent, uuid, positionComponent);
        } else {
            return String.format("%d, %s %s", itemId, uuid, itemComponent);
        }
    }
}
