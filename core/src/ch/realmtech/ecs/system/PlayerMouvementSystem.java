package ch.realmtech.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.ecs.ECSEngine;
import ch.realmtech.ecs.component.MovementComponent;
import ch.realmtech.ecs.component.PlayerComponent;
import ch.realmtech.ecs.component.PossitionComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlayerMouvementSystem extends IteratingSystem {

    private final RealmTech context;
    public PlayerMouvementSystem(RealmTech context) {
        super(Family.all(PlayerComponent.class)
                .all(PossitionComponent.class)
                .all(MovementComponent.class)
                .get());
        this.context = context;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final PlayerComponent playerComponent = ECSEngine.PLAYER_COMPONENT_MAPPER.get(entity);
        final PossitionComponent possitionComponent = ECSEngine.POSSITION_COMPONENT_MAPPER.get(entity);
        final MovementComponent movementComponent = ECSEngine.MOVEMENT_COMPONENT_MAPPER.get(entity);

        // TODO mettre une vrai gestion des input
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            possitionComponent.y += deltaTime * movementComponent.speedUnite;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            possitionComponent.x -= deltaTime * movementComponent.speedUnite;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            possitionComponent.y -= deltaTime * movementComponent.speedUnite;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            possitionComponent.x += deltaTime * movementComponent.speedUnite;
        }
    }
}
