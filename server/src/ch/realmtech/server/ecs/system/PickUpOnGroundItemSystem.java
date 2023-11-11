package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.*;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;

/**
 * Test si un item est dans une distance pour être pris par une entité. Généralement, un item qui est à une distance
 * pour être pris par le joueur
 */
@All(PickerGroundItemComponent.class)
public class PickUpOnGroundItemSystem extends IteratingSystem {
    private final static String TAG = PickUpOnGroundItemSystem.class.getSimpleName();
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PickerGroundItemComponent> mPicker;
    private ComponentMapper<ItemBeingPickComponent> mBeingPick;

    @Override
    protected void process(int pickerId) {
        PositionComponent positionPickerComponent = mPosition.get(pickerId);
        PickerGroundItemComponent pickerComponent = mPicker.get(pickerId);

        IntBag items = world.getAspectSubscriptionManager().get(Aspect.all(ItemComponent.class, Box2dComponent.class, PositionComponent.class)).getEntities();
        int[] itemsData = items.getData();
        for (int i = 0; i < items.size(); i++) {
            int itemId = itemsData[i];
            PositionComponent positionItemComponent = mPosition.get(itemId);
            float distance = Vector2.dst2(positionPickerComponent.x, positionPickerComponent.y, positionItemComponent.x, positionItemComponent.y);
            if (distance <= pickerComponent.magnetRange) {
                if (!mBeingPick.has(itemId)) {
                    world.edit(itemId).create(ItemBeingPickComponent.class).set(pickerId); // ajout une animation qui déplace l'item vers le joueur
                }
            }
        }
    }
}
