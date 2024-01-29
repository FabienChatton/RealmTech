package ch.realmtech.core.game.ecs.plugin.strategy;

import ch.realmtech.server.ecs.plugin.SystemServerTickSlave;
import com.artemis.BaseSystem;
import com.artemis.InvocationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class TickEmulationInvocationStrategy extends InvocationStrategy {
    private final Logger logger = LoggerFactory.getLogger(TickEmulationInvocationStrategy.class);

    private final List<Float> tickBeatsElapseTime;

    private BaseSystem[] normalSystem;

    private BaseSystem[] tickSlaveSystem;
    public TickEmulationInvocationStrategy() {
        tickBeatsElapseTime = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    protected void initialize() {
        super.initialize();
        normalSystem = getSystemWithPredicateClass(((baseSystem) -> !baseSystem.getClass().isAnnotationPresent(SystemServerTickSlave.class)));
        tickSlaveSystem = getSystemWithPredicateClass(((baseSystem) -> baseSystem.getClass().isAnnotationPresent(SystemServerTickSlave.class)));
    }

    @Override
    protected void process() {
        int t;
        synchronized (tickBeatsElapseTime) {
            for (t = 0; t < tickBeatsElapseTime.size(); t++) {
                float elapse = tickBeatsElapseTime.get(t);
                world.setDelta(elapse);
                for (int j = 0; j < tickSlaveSystem.length; j++) {
                    if (!tickSlaveSystem[j].isEnabled()) {
                        continue;
                    }
                    updateEntityStates();
                    tickSlaveSystem[j].process();
                }
            }
            tickBeatsElapseTime.clear();
        }

        if (t > 60) {
            logger.info("Client is behind {} ticks from server", t);
        }

        for (int i = 0; i < normalSystem.length; i++) {
            if (!normalSystem[i].isEnabled()) {
                continue;
            }
            updateEntityStates();
            normalSystem[i].process();
        }

        updateEntityStates();
    }

    private BaseSystem[] getSystemWithPredicateClass(Predicate<BaseSystem> predicate) {
        BaseSystem[] systemsData = systems.getData();
        int systemValideCount = 0;

        for (int i = 0; i < systems.size(); i++) {
            BaseSystem system = systemsData[i];
            if (predicate.test(system)) {
                systemValideCount++;
            }
        }
        BaseSystem[] systemValide = new BaseSystem[systemValideCount];
        for (int i = 0, j = 0; i < systems.size() && j < systemValide.length; i++) {
            BaseSystem system = systemsData[i];
            if (predicate.test(system)) {
                systemValide[j++] = system;
            }
        }
        return systemValide;
    }

    public void addTickBeatDeltaTime(float deltaTime) {
        synchronized (tickBeatsElapseTime) {
            tickBeatsElapseTime.add(deltaTime);
        }
    }
}
