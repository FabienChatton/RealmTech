package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.PickUpOnGroundItemComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import ch.realmtech.game.ecs.component.PositionComponent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;

@All({PickUpOnGroundItemComponent.class, PositionComponent.class})
public class PickUpOnGroundItemSystem extends IteratingSystem {
    private ComponentMapper<PickUpOnGroundItemComponent> mPickUp;
    private ComponentMapper<PositionComponent> mPosition;
    @Override
    protected void process(int entityId) {
        PickUpOnGroundItemComponent pickUpOnGroundItemComponent = mPickUp.create(entityId);
        PositionComponent pickerPositionComponent = mPosition.create(entityId);
        Vector2 pickerPosition = new Vector2(pickerPositionComponent.x,pickerPositionComponent.y);
        IntBag itemCanPickOp = world.getAspectSubscriptionManager().get(Aspect.all(ItemComponent.class, PositionComponent.class)).getEntities();
        for (int item : itemCanPickOp.getData()) {
            // pourquoi le joueur se trouve dans les items ?
            if (item !=  world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG)) {
                PositionComponent itemPositionComponent = mPosition.get(item);
                if (itemPositionComponent != null) {
                    Vector2 itemPosition = new Vector2(itemPositionComponent.x, itemPositionComponent.y);
                    if (pickerPosition.dst2(itemPosition) <= pickUpOnGroundItemComponent.magnetRange) {
                        world.getSystem(InventoryManager.class).addItemToPlayerInventory(item, entityId);
                        break;
                    }
                }
            }
        }
    }
}
