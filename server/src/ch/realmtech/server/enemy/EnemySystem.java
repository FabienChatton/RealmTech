package ch.realmtech.server.enemy;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;

@All(EnemyComponent.class)
public class EnemySystem extends IteratingSystem {
    private ComponentMapper<EnemyComponent> mEnemy;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Override
    protected void process(int entityId) {
        EnemyComponent enemyComponent = mEnemy.get(entityId);
        enemyComponent.getIaTestSteerable().update(world.getDelta());
    }
}
