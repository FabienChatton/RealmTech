package ch.realmtech.core.game.ecs.plugin.strategy;

import com.artemis.BaseSystem;
import com.artemis.World;

import java.util.List;

public class SystemDisableOnInventoryOpen implements InGameSystemOnInventoryOpen {
    private final List<Class<? extends BaseSystem>> inGameSystem;

    public SystemDisableOnInventoryOpen(List<Class<? extends BaseSystem>> inGameSystem) {
        this.inGameSystem = inGameSystem;
    }

    @Override
    public void onInventoryOpen(World world) {
        for (Class<? extends BaseSystem> inGameSystem : inGameSystem) {
            BaseSystem system = world.getSystem(inGameSystem);
            system.setEnabled(false);
        }
    }

    @Override
    public void onInventoryClose(World world) {
        for (Class<? extends BaseSystem> inGameSystem : inGameSystem) {
            BaseSystem system = world.getSystem(inGameSystem);
            system.setEnabled(true);
        }
    }
}
