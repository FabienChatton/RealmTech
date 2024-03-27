package ch.realmtech.core.game.ecs.plugin.strategy;

import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
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
        SystemsAdminCommun systemsAdmin = world.getRegistered("systemsAdmin");
        for (Class<? extends BaseSystem> inGameSystem : inGameSystem) {
            systemsAdmin.getCustomSystem(inGameSystem).setEnabled(false);
        }
    }

    @Override
    public void onInventoryClose(World world) {
        SystemsAdminCommun systemsAdmin = world.getRegistered("systemsAdmin");
        for (Class<? extends BaseSystem> inGameSystem : inGameSystem) {
            systemsAdmin.getCustomSystem(inGameSystem).setEnabled(true);
        }
    }
}
