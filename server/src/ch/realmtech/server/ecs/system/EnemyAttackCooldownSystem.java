package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.EnemyAttackCooldownComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All(EnemyAttackCooldownComponent.class)
public class EnemyAttackCooldownSystem extends IteratingSystem {
    private ComponentMapper<EnemyAttackCooldownComponent> mMobAttackCooldown;

    @Override
    protected void process(int entityId) {
        EnemyAttackCooldownComponent enemyAttackCooldownComponent = mMobAttackCooldown.get(entityId);
        enemyAttackCooldownComponent.setRemainingTick(enemyAttackCooldownComponent.getRemainingTick() - 1);
        if (enemyAttackCooldownComponent.getRemainingTick() <= 0) {
            enemyAttackCooldownComponent.getOnEnd().run();
            mMobAttackCooldown.remove(entityId);
        }
    }
}
