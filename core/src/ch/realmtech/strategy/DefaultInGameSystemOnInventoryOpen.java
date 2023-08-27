package ch.realmtech.strategy;

import com.artemis.BaseSystem;
import com.artemis.World;

public class DefaultInGameSystemOnInventoryOpen<T extends BaseSystem> implements InGameSystemOnInventoryOpen {
    private final Class<T>[] inGameSystem;


    @SafeVarargs
    public DefaultInGameSystemOnInventoryOpen(Class<T>... inGameSystem) {
        this.inGameSystem = inGameSystem;
    }

    @Override
    public void disableInGameSystemOnPause(World world) {
        for (Class<T> inGameSystem : inGameSystem) {
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
