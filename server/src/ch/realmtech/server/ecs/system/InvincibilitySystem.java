package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.InvincibilityComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;

@All(InvincibilityComponent.class)
public class InvincibilitySystem extends IteratingSystem {
    private ComponentMapper<InvincibilityComponent> mInvincibility;

    @Override
    protected void process(int entityId) {
        InvincibilityComponent invincibilityComponent = mInvincibility.get(entityId);
        invincibilityComponent.setRemainingTick(invincibilityComponent.getRemainingTick() - 1);
        if (invincibilityComponent.getRemainingTick() <= 0) {
            mInvincibility.remove(entityId);
        }
    }

}
