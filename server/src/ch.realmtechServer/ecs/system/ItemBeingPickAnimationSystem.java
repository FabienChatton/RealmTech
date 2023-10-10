package ch.realmtechServer.ecs.system;

import ch.realmtechServer.ecs.component.Box2dComponent;
import ch.realmtechServer.ecs.component.ItemBeingPickComponent;
import ch.realmtechServer.ecs.component.PositionComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.Body;

@All(ItemBeingPickComponent.class)
public class ItemBeingPickAnimationSystem extends IteratingSystem {
    private final static String TAG = ItemBeingPickAnimationSystem.class.getSimpleName();
    private ComponentMapper<ItemBeingPickComponent> mItemPick;
    private ComponentMapper<Box2dComponent> mBox2D;
    private ComponentMapper<PositionComponent> mPosition;
    private final static float FORCE_D_ATTRACTION = 10f;

    @Override
    protected void process(int itemId) {
        if (!mBox2D.has(itemId)) return;
        ItemBeingPickComponent itemBeingPickComponent = mItemPick.get(itemId);
        Box2dComponent box2dComponent = mBox2D.get(itemId);
        Body itemBody = box2dComponent.body;
        PositionComponent positionComponent = mPosition.get(itemBeingPickComponent.picker);
        itemBody.applyLinearImpulse(
                (positionComponent.x - itemBody.getWorldCenter().x) * FORCE_D_ATTRACTION,
                (positionComponent.y - itemBody.getWorldCenter().y) * FORCE_D_ATTRACTION,
                itemBody.getWorldCenter().x,
                itemBody.getWorldCenter().y,
                true
        );
    }
}
