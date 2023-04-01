package ch.realmtech.ecs.system;

import ch.realmtech.ecs.ECSEngine;
import ch.realmtech.ecs.component.Box2dComponent;
import ch.realmtech.ecs.component.PossitionComponent;
import ch.realmtech.ecs.component.TextureComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class UpdateBox2dWithTextureSystem extends IteratingSystem {


    public UpdateBox2dWithTextureSystem() {
        super(Family.all(Box2dComponent.class)
                .all(TextureComponent.class)
                .get()
                ,5
        );
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PossitionComponent possitionComponent = ECSEngine.POSSITION_COMPONENT_MAPPER.get(entity);
        Box2dComponent box2dComponent = ECSEngine.BOX2D_COMPONENT_MAPPER.get(entity);
        possitionComponent.x = box2dComponent.body.getWorldCenter().x - box2dComponent.widthWorld / 2;
        possitionComponent.y = box2dComponent.body.getWorldCenter().y - box2dComponent.heightWorld / 2;
    }
}
