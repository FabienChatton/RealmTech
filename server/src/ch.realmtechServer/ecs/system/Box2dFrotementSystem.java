package ch.realmtechServer.ecs.system;

import ch.realmtechServer.ecs.component.Box2dComponent;
import ch.realmtechServer.ecs.component.PlayerComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

@All(Box2dComponent.class)
@Exclude(PlayerComponent.class)
public class Box2dFrotementSystem extends IteratingSystem {
    private ComponentMapper<Box2dComponent> mBox2d;

    @Override
    protected void process(int entityId) {
        Box2dComponent box2dComponent = mBox2d.get(entityId);
        Vector2 linearVelocity = box2dComponent.body.getLinearVelocity();
        Vector2 worldCenter = box2dComponent.body.getWorldCenter();
        box2dComponent.body.applyLinearImpulse(new Vector2(-linearVelocity.x / 10, -linearVelocity.y / 10), worldCenter, true);
    }
}
