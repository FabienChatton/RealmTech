package ch.realmtech.game.ecs;

import com.artemis.BaseSystem;
import com.artemis.InvocationStrategy;
import com.artemis.utils.Bag;

public class ServerInvocationStrategy extends InvocationStrategy implements InvocationStrategyServer {
    private long time = System.currentTimeMillis();
    private final static long TIME_LAPS_MILLIS = 15;
    private Bag<BaseSystem> serverSystems;

    public ServerInvocationStrategy() {
        serverSystems = new Bag<>();
    }

    @Override
    protected void process() {
        boolean processServer = false;
        if (System.currentTimeMillis() - time >= TIME_LAPS_MILLIS) {
            time = System.currentTimeMillis();
            processServer = true;
        }

        BaseSystem[] systemsData = systems.getData();
        for (int i = 0, s = systems.size(); s > i; i++) {
            if (disabled.get(i))
                continue;

            updateEntityStates();
            if (!serverSystems.contains(systemsData[i])) {
                systemsData[i].process();
            } else {
                if (processServer) {
                    systemsData[i].process();
                }
            }
        }

    }

    @Override
    public void registerServerSystem(BaseSystem serverSystem) {
        serverSystems.add(serverSystem);
    }
}
