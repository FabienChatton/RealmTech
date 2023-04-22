package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.Box2dComponent;
import ch.realmtech.game.ecs.component.ItemBeingPickComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;

@All({ItemBeingPickComponent.class, Box2dComponent.class})
public class ItemBeingPickAnimationSystem extends IteratingSystem {
    private final static String TAG = ItemBeingPickAnimationSystem.class.getSimpleName();
    private ComponentMapper<ItemBeingPickComponent> mItemPick;
    private ComponentMapper<Box2dComponent> mBox2D;
    private final static float FORCE_D_ATTRACTION = 10;

    @Override
    protected void process(int itemId) {
        ItemBeingPickComponent itemBeingPickComponent = mItemPick.get(itemId);
        Box2dComponent box2dComponent = mBox2D.get(itemId);
        Body itemBody = box2dComponent.body;
        Body playerBody = mBox2D.get(world.getSystem(TagManager.class).getEntityId(PlayerComponent.TAG)).body;
        itemBody.applyLinearImpulse(
                (playerBody.getWorldCenter().x - itemBody.getWorldCenter().x) * FORCE_D_ATTRACTION,
                (playerBody.getWorldCenter().y - itemBody.getWorldCenter().y) * FORCE_D_ATTRACTION,
                itemBody.getWorldCenter().x,
                itemBody.getWorldCenter().y,
                true
        );
        Gdx.app.debug(TAG, "l'item (" + itemId + ") est attire vers l'entite (" + itemBeingPickComponent.picker +")");
    }
}
