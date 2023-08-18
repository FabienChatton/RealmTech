package ch.realmtech.strategy;

import com.artemis.BaseSystem;
import com.artemis.InvocationStrategy;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.TimeUtils;

public class ServerInvocationStrategy extends InvocationStrategy implements InvocationStrategyServer {
    private long time = System.currentTimeMillis();
    public final static long TIME_LAPS_MILLIS = 20;
    private Bag<BaseSystem> serverSystems;
    private float delta = 0;

    public ServerInvocationStrategy() {
        serverSystems = new Bag<>();
    }

    @Override
    protected void process() {
        boolean processServer = false;
        if (System.currentTimeMillis() - time >= TIME_LAPS_MILLIS) {
            long deltaMillis = TimeUtils.timeSinceMillis(time);
            delta = deltaMillis / 1000f;
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

    @Override
    public float getDeltaTime() {
        return delta;
    }
}
