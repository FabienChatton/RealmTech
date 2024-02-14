package ch.realmtech.core.game.ecs.system;


import ch.realmtech.server.ecs.component.PlayerComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.SystemServerTickSlave;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

@SystemServerTickSlave
@All(PlayerComponent.class)
public class PlayerMouvementTextureSystem extends IteratingSystem {
    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<PositionComponent> mPos;

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void process(int entityId) {
        PlayerComponent playerComponent = mPlayer.get(entityId);
        PositionComponent positionComponent = mPos.get(entityId);


        playerComponent.oldPoss.add(new Vector2(positionComponent.x, positionComponent.y));
        playerComponent.moveLeft = playerComponent.oldPoss.get(0).x > positionComponent.x;
        playerComponent.moveDown = playerComponent.oldPoss.get(0).y > positionComponent.y;
        playerComponent.moveUp = playerComponent.oldPoss.get(0).y < positionComponent.y;
        playerComponent.moveRight = playerComponent.oldPoss.get(0).x < positionComponent.x;

    }
}

