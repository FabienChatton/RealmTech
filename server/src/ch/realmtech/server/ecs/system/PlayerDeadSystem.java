package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.*;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;

@One({PlayerConnexionComponent.class, PlayerComponent.class, Box2dComponent.class})
@All({LifeComponent.class})
public class PlayerDeadSystem extends IteratingSystem {
    private ComponentMapper<LifeComponent> mLife;
    private ComponentMapper<PlayerDeadComponent> mDead;
    private ComponentMapper<Box2dComponent> mBox2d;

    @Override
    protected void process(int entityId) {
        LifeComponent playerLife = mLife.get(entityId);
        if (mDead.has(entityId)) {
            if (playerLife.getHeart() > 0) {
                mDead.remove(entityId);
            } else {
                // every tick on death
                Box2dComponent box2dComponent = mBox2d.get(entityId);
                box2dComponent.body.applyLinearImpulse(0, 0, 0, 0, true);
            }
        } else {
            if (playerLife.getHeart() <= 0) {
                mDead.create(entityId);
            }
        }
    }
}
