package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.Box2dComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import ch.realmtech.game.ecs.component.PositionComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;

@All({PositionComponent.class, Box2dComponent.class, PlayerComponent.class})
public class CameraFollowPlayerSystem extends IteratingSystem {
    @Wire(name = "gameCamera")
    OrthographicCamera gameCamera;

    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<Box2dComponent> mBox2d;

    @Override
    protected void process(int entityId) {
        PositionComponent positionComponent = mPosition.get(entityId);
        Box2dComponent box2dComponent = mBox2d.get(entityId);
        gameCamera.position.x = positionComponent.x + box2dComponent.widthWorld/2;
        gameCamera.position.y = positionComponent.y + box2dComponent.heightWorld/2;
    }
}
