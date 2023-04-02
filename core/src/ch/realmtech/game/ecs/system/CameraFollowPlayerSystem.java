package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.ECSEngine;
import ch.realmtech.game.ecs.component.Box2dComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import ch.realmtech.game.ecs.component.PossitionComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraFollowPlayerSystem extends IteratingSystem {
    private final OrthographicCamera gameCamera;
    public CameraFollowPlayerSystem(final OrthographicCamera gameCamera) {
        super(Family
                .all(PlayerComponent.class)
                .all(PossitionComponent.class)
                .all(Box2dComponent.class)
                .get(),7);
        this.gameCamera = gameCamera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PossitionComponent possitionComponent = ECSEngine.POSSITION_COMPONENT_MAPPER.get(entity);
        Box2dComponent box2dComponent = ECSEngine.BOX2D_COMPONENT_MAPPER.get(entity);
        gameCamera.position.x = possitionComponent.x + box2dComponent.widthWorld/2;
        gameCamera.position.y = possitionComponent.y + box2dComponent.heightWorld/2;
    }
}
