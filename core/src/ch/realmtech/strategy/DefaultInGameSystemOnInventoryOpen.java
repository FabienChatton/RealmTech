package ch.realmtech.strategy;

import com.artemis.BaseSystem;
import com.artemis.World;

public class DefaultInGameSystemOnInventoryOpen implements InGameSystemOnInventoryOpen {
    private final Class[] inGameSystem;


    public DefaultInGameSystemOnInventoryOpen(Class... inGameSystem) {
        this.inGameSystem = inGameSystem;
    }

    @Override
    public void disableInGameSystemOnPause(World world) {
        for (Class inGameSystem : inGameSystem) {
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
