package ch.realmtechServer.item;

import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.component.PositionComponent;
import com.artemis.ComponentMapper;
import com.artemis.World;

public class ItemInfoHelper {
    public static String dumpItem(World world, int itemId) {
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);
        ComponentMapper<PositionComponent> mPos = world.getMapper(PositionComponent.class);
        ItemComponent itemComponent = mItem.get(itemId);

        if (mPos.has(itemId)) {
            PositionComponent positionComponent = mPos.get(itemId);
            return String.format("%d, %s, %s", itemId, itemComponent, positionComponent);
        } else {
            return String.format("%d, %s", itemId, itemComponent);
        }
    }
}
