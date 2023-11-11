package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.PlayerComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.Bag;
import com.badlogic.gdx.math.Vector2;

@All({Box2dComponent.class, PositionComponent.class})
@Exclude(PlayerConnexionComponent.class)
public class Box2dFrotementSystem extends IteratingSystem {
    private ComponentMapper<Box2dComponent> mBox2d;

    @Override
    protected void process(int entityId) {
        Box2dComponent box2dComponent = mBox2d.get(entityId);
        if (box2dComponent == null ||box2dComponent.body == null) return;
        if (box2dComponent.body.isAwake()) {
            Vector2 linearVelocity = box2dComponent.body.getLinearVelocity();
            Vector2 worldCenter = box2dComponent.body.getWorldCenter();
            box2dComponent.body.applyLinearImpulse(new Vector2(-linearVelocity.x / 10, -linearVelocity.y / 10), worldCenter, true);
        }
    }
}
