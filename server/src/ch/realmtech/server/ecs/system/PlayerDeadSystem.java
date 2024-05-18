package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.LifeComponent;
import ch.realmtech.server.ecs.component.PlayerComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PlayerDeadComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;

@One({PlayerConnexionComponent.class, PlayerComponent.class})
@All({LifeComponent.class})
public class PlayerDeadSystem extends IteratingSystem {
    private ComponentMapper<LifeComponent> mLife;
    private ComponentMapper<PlayerDeadComponent> mDead;

    @Override
    protected void process(int entityId) {
        LifeComponent playerLife = mLife.get(entityId);
        if (mDead.has(entityId)) {
            if (playerLife.getHeart() > 0) {
                mDead.remove(entityId);
            }
        } else {
            if (playerLife.getHeart() <= 0) {
                mDead.create(entityId);
            }
        }
    }
}
