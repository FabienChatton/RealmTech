package ch.realmtech.server.ecs.system;

import com.artemis.BaseSystem;

public class TimeSystem extends BaseSystem {
    private float accumulatedDelta;
    @Override
    protected void processSystem() {
        accumulatedDelta += world.getDelta();
    }

    public void setAccumulatedDelta(float accumulatedDelta) {
        this.accumulatedDelta = accumulatedDelta;
    }

    public float getAccumulatedDelta() {
        return accumulatedDelta;
    }
}
