package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.ItemBeingPickComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.PickUpOnGroundItemComponent;
import ch.realmtech.game.ecs.component.PositionComponent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;

/**
 * Se system permet de connaitre si un item peut être pris par une entité qui peut prendre les items au sol
 * généralement, le joueur prend un item au sol.
 */
@All({PickUpOnGroundItemComponent.class, PositionComponent.class})
public class PickUpOnGroundItemSystem extends IteratingSystem {
    private ComponentMapper<PickUpOnGroundItemComponent> mPickUp;
    private ComponentMapper<PositionComponent> mPosition;

    @Override
    protected void process(int playerId) {
        PickUpOnGroundItemComponent pickUpOnGroundItemComponent = mPickUp.create(playerId);
        PositionComponent pickerPositionComponent = mPosition.create(playerId);

        Vector2 pickerPosition = new Vector2(pickerPositionComponent.x,pickerPositionComponent.y);
        IntBag itemCanPickOp = world.getAspectSubscriptionManager().get(Aspect.all(ItemComponent.class, PositionComponent.class)).getEntities();
        for (int itemId : itemCanPickOp.getData()) {
            if (itemId != 0) {
                PositionComponent itemPositionComponent = mPosition.get(itemId);
                if (itemPositionComponent != null) {
                    Vector2 itemPosition = new Vector2(itemPositionComponent.x, itemPositionComponent.y);
                    if (pickerPosition.dst2(itemPosition) <= pickUpOnGroundItemComponent.magnetRange) {
                        world.edit(itemId).create(ItemBeingPickComponent.class).set(playerId); // ajout une animation qui déplace l'item vers le joueur
                        break;
                    }
                }
            }
        }
    }
}
