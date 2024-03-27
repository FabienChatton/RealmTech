package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.MobAttackCooldownComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All(MobAttackCooldownComponent.class)
public class MobAttackCooldownSystem extends IteratingSystem {
    private ComponentMapper<MobAttackCooldownComponent> mMobAttackCooldown;

    @Override
    protected void process(int entityId) {
        MobAttackCooldownComponent mobAttackCooldownComponent = mMobAttackCooldown.get(entityId);
        mobAttackCooldownComponent.setRemainingTick(mobAttackCooldownComponent.getRemainingTick() - 1);
        if (mobAttackCooldownComponent.getRemainingTick() <= 0) {
            mobAttackCooldownComponent.getOnEnd().run();
            mMobAttackCooldown.remove(entityId);
        }
    }
}
