package ch.realmtech.core.game.ecs.plugin.strategy;

import com.artemis.World;

public interface InGameSystemOnInventoryOpen {
    void onInventoryOpen(World world);

    void onInventoryClose(World world);
}
