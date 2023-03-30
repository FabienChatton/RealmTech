package ch.realmtech.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.ecs.ECSEngine;
import ch.realmtech.ecs.component.Box2dComponent;
import ch.realmtech.ecs.component.MovementComponent;
import ch.realmtech.ecs.component.PlayerComponent;
import ch.realmtech.ecs.component.PossitionComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
                .get()
        ,-10);
        this.context = context;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        final PlayerComponent playerComponent = ECSEngine.PLAYER_COMPONENT_MAPPER.get(entity);
        final PossitionComponent possitionComponent = ECSEngine.POSSITION_COMPONENT_MAPPER.get(entity);
        final MovementComponent movementComponent = ECSEngine.MOVEMENT_COMPONENT_MAPPER.get(entity);
        final Box2dComponent box2dComponent = ECSEngine.BOX2D_COMPONENT_MAPPER.get(entity);
        // TODO mettre une vrai gestion des input
        xFactor = 0;
        yFactor = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            directionChange = true;
            yFactor = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            directionChange = true;
            xFactor = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            directionChange = true;
            yFactor = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            directionChange = true;
            xFactor = 1;
        }

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
        possitionComponent.x = box2dComponent.body.getWorldCenter().x - box2dComponent.widthWorld / 2;
        possitionComponent.y = box2dComponent.body.getWorldCenter().y - box2dComponent.heightWorld / 2;
//        if (!(Gdx.input.isKeyPressed(Input.Keys.W) | Gdx.input.isKeyPressed(Input.Keys.A) | Gdx.input.isKeyPressed(Input.Keys.S) | Gdx.input.isKeyPressed(Input.Keys.D))) {
//            directionChange = false;
//        }
    }
}
