package ch.realmtech.core.game.ecs.system;


import ch.realmtech.server.ecs.component.MouvementComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.plugin.SystemServerTickSlave;
import ch.realmtech.server.ecs.system.PlayerMouvementSystemServer;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

@SystemServerTickSlave
@All({MouvementComponent.class, PositionComponent.class})
public class PlayerMouvementTextureSystem extends IteratingSystem {
    private ComponentMapper<MouvementComponent> mMovement;
    private ComponentMapper<PositionComponent> mPos;

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void process(int entityId) {
        MouvementComponent mouvementComponent = mMovement.get(entityId);
        PositionComponent positionComponent = mPos.get(entityId);


        mouvementComponent.oldPoss.add(new Vector2(positionComponent.x, positionComponent.y));
        mouvementComponent.lastDirection = PlayerMouvementSystemServer.getKeysInputPlayerMouvement(
                mouvementComponent.oldPoss.get(0).x > positionComponent.x,
                mouvementComponent.oldPoss.get(0).y > positionComponent.y,
                mouvementComponent.oldPoss.get(0).y < positionComponent.y,
                mouvementComponent.oldPoss.get(0).x < positionComponent.x

        );
    }
}

