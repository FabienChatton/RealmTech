package ch.realmtechCommuns.ecs.system;

import ch.realmtechCommuns.ecs.component.*;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Vector2;

/**
 * Test si un item est dans une distance pour être pris par une entité. Généralement, un item qui est à une distance
 * pour être pris par le joueur
 */
@All({ItemComponent.class, PositionComponent.class, Box2dComponent.class, PositionComponent.class})
@Exclude(ItemBeingPickComponent.class)
public class PickUpOnGroundItemSystem extends IteratingSystem {
    private final static String TAG = PickUpOnGroundItemSystem.class.getSimpleName();
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PickerGroundItemComponent> mPicker;
    private ComponentMapper<ItemBeingPickComponent> mBeingPick;

    @Override
    protected void process(int itemId) {
        if (mBeingPick.has(itemId)) return;
        PositionComponent positionItemComponent = mPosition.get(itemId);
        ItemComponent itemComponent = mItem.get(itemId);

        IntBag itemPickers = world.getAspectSubscriptionManager().get(Aspect.all(PlayerComponent.class, PickerGroundItemComponent.class, PositionComponent.class)).getEntities();
        for (int itemPicker : itemPickers.getData()) {
            if (itemPicker == 0) continue;
            PickerGroundItemComponent pickerGroundItemComponent = mPicker.get(itemPicker);
            PositionComponent positionPickerComponent = mPosition.get(itemPicker);
            float distance = Vector2.dst2(positionPickerComponent.x, positionPickerComponent.y, positionItemComponent.x, positionItemComponent.y);
            if (distance <= pickerGroundItemComponent.magnetRange) {
                world.edit(itemId).create(ItemBeingPickComponent.class).set(itemPicker); // ajout une animation qui déplace l'item vers le joueur
                break;
            }
        }
    }
}
