package ch.realmtech.core.game.ecs.system;


import ch.realmtech.server.ecs.component.PlayerComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All(PlayerComponent.class)
public class PlayerMouvementTextureSystem extends IteratingSystem {
    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<PositionComponent> mPos;
    @Override
    protected void process(int entityId) {
        PlayerComponent playerComponent = mPlayer.get(entityId);
        PositionComponent positionComponent = mPos.get(entityId);
        playerComponent.moveLeft = playerComponent.oldPos.x > positionComponent.x;
        playerComponent.moveDown = playerComponent.oldPos.y > positionComponent.y;
        playerComponent.moveUp = playerComponent.oldPos.y < positionComponent.y;
        playerComponent.moveRight = playerComponent.oldPos.x < positionComponent.x;

        playerComponent.oldPos.set(positionComponent.x, positionComponent.y);
    }
}