package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.ECSEngine;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.input.InputMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

public class PlayerMouvementSystem extends IteratingSystem {
    private boolean directionChange = false;
    private float xFactor = 0;
    private float yFactor = 0;
    private final RealmTech context;
    public PlayerMouvementSystem(RealmTech context) {
        super(Family.all(PlayerComponent.class)
                .all(MovementComponent.class)
                .all(PossitionComponent.class)
                .all(Box2dComponent.class)
                .all(TextureComponent.class)
                .get()
        ,-10);
        this.context = context;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final MovementComponent movementComponent = ECSEngine.MOVEMENT_COMPONENT_MAPPER.get(entity);
        final Box2dComponent box2dComponent = ECSEngine.BOX2D_COMPONENT_MAPPER.get(entity);
        final PlayerComponent playerComponent = ECSEngine.PLAYER_COMPONENT_MAPPER.get(entity);
        // TODO mettre une vrai gestion des input
        xFactor = 0;
        yFactor = 0;
        if (context.getInputManager().isKeyPressed(InputMapper.moveForward.key)) {
            directionChange = true;
            yFactor = 1;
        }
        if (context.getInputManager().isKeyPressed(InputMapper.moveLeft.key)) {
            directionChange = true;
            xFactor = -1;
        }
        if (context.getInputManager().isKeyPressed(InputMapper.moveBack.key)) {
            directionChange = true;
            yFactor = -1;
        }
        if (context.getInputManager().isKeyPressed(InputMapper.moveRight.key)) {
            directionChange = true;
            xFactor = 1;
        }

//        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//            directionChange = true;
//            yFactor = 1;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//            directionChange = true;
//            xFactor = -1;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            directionChange = true;
//            yFactor = -1;
//        }
//        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
//            directionChange = true;
//            xFactor = 1;
//        }

        if (directionChange) {
            directionChange = false;
            movementComponent.speed.x = xFactor * movementComponent.speedMeterParSeconde;
            movementComponent.speed.y = yFactor * movementComponent.speedMeterParSeconde;
        } else {
            movementComponent.speed.x = 0;
            movementComponent.speed.y = 0;
            xFactor = 0;
            yFactor = 0;
        }

        final Vector2 worldCenter = box2dComponent.body.getWorldCenter();
        box2dComponent.body.applyLinearImpulse(
                (movementComponent.speed.x - box2dComponent.body.getLinearVelocity().x) * box2dComponent.body.getMass(),
                (movementComponent.speed.y - box2dComponent.body.getLinearVelocity().y) * box2dComponent.body.getMass(),
                worldCenter.x,
                worldCenter.y,
                true
        );
    }
}
