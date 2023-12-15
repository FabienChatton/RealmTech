package ch.realmtech.core.game.ecs.plugin.strategy;

import ch.realmtech.core.game.ecs.plugin.SystemTickEmulation;
import ch.realmtech.server.tick.TickThread;
import com.artemis.BaseSystem;
import com.artemis.InvocationStrategy;

public class TickEmulationInvocationStrategy extends InvocationStrategy {
    private long time = System.currentTimeMillis();

    public TickEmulationInvocationStrategy() { }

    @Override
    protected void process() {
        boolean processTickSystem = false;
        if (System.currentTimeMillis() - time >= TickThread.TIME_TICK_LAPS_MILLIS) {
            time = System.currentTimeMillis();
            processTickSystem = true;
        }

        BaseSystem[] systemsData = systems.getData();
        for (int i = 0, s = systems.size(); s > i; i++) {
            if (disabled.get(i))
                continue;

            updateEntityStates();
            BaseSystem system = systemsData[i];
            if (system.getClass().isAnnotationPresent(SystemTickEmulation.class)) {
                if (processTickSystem) {
                    system.process();
                }
            } else {
                system.process();
            }
        }

    }
}
