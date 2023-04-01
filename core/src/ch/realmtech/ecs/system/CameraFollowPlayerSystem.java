package ch.realmtech.ecs.system;

import ch.realmtech.ecs.ECSEngine;
import ch.realmtech.ecs.component.PlayerComponent;
import ch.realmtech.ecs.component.PossitionComponent;
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
                .get(),7);
        this.gameCamera = gameCamera;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PossitionComponent possitionComponent = ECSEngine.POSSITION_COMPONENT_MAPPER.get(entity);
        gameCamera.position.x = possitionComponent.x;
        gameCamera.position.y = possitionComponent.y;
    }
}
