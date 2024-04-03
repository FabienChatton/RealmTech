package ch.realmtech.core.game.ecs.system;

import ch.realmtech.server.ecs.plugin.SystemServerTickSlave;
import com.artemis.BaseSystem;

@SystemServerTickSlave
public class TickSlaveEmulationSystem extends BaseSystem {
    private long tick;

    @Override
    protected void initialize() {
        tick = 0;
    }

    @Override
    protected void processSystem() {
        tick++;
    }

    public long getTick() {
        return tick;
    }
}
