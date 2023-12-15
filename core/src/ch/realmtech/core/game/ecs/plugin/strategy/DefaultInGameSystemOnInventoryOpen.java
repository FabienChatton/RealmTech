package ch.realmtech.core.game.ecs.plugin.strategy;

import com.artemis.BaseSystem;
import com.artemis.World;

import java.util.List;

public class DefaultInGameSystemOnInventoryOpen implements InGameSystemOnInventoryOpen {
    private final List<Class<? extends BaseSystem>> inGameSystem;

    public DefaultInGameSystemOnInventoryOpen(List<Class<? extends BaseSystem>> inGameSystem) {
        this.inGameSystem = inGameSystem;
    }

    @Override
    public void disableInGameSystemOnPause(World world) {
        for (Class<? extends BaseSystem> inGameSystem : inGameSystem) {
            BaseSystem system = world.getSystem(inGameSystem);
            if (system.isEnabled()) {
                system.setEnabled(false);
            }
        }
    }

    @Override
    public void activeInGameSystemOnPause(World world) {
        for (Class<? extends BaseSystem> inGameSystem : inGameSystem) {
            BaseSystem system = world.getSystem(inGameSystem);
            if (!system.isEnabled()) {
                system.setEnabled(true);
            }
        }
    }
}
