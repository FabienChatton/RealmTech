package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.Box2dComponent;
import ch.realmtech.game.ecs.component.PositionComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All({Box2dComponent.class, PositionComponent.class})
public class UpdateBox2dWithTextureSystem extends IteratingSystem {
    ComponentMapper<Box2dComponent> mbox2d;
    ComponentMapper<PositionComponent> mPosition;

    @Override
    protected void process(int entityId) {
        Box2dComponent box2dComponent = mbox2d.create(entityId);
        PositionComponent positionComponent = mPosition.create(entityId);
        positionComponent.x = box2dComponent.body.getWorldCenter().x - box2dComponent.widthWorld / 2;
        positionComponent.y = box2dComponent.body.getWorldCenter().y - box2dComponent.heightWorld / 2;
    }
}
