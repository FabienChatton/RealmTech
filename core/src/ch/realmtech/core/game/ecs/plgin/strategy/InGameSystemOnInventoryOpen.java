package ch.realmtech.core.game.ecs.plgin.strategy;

import com.artemis.World;

public interface InGameSystemOnInventoryOpen {
    void disableInGameSystemOnPause(World world);

    void activeInGameSystemOnPause(World world);
}
