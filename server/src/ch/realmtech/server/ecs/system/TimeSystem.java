package ch.realmtech.server.ecs.system;

import com.artemis.BaseSystem;
import com.badlogic.gdx.math.Interpolation;

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

    private final static Interpolation.Exp interpolation = new Interpolation.Exp(2, 10);
    public static float getAlpha(float time) {
        return Math.max(0.1f, interpolation.apply((float) (Math.sin(Math.toRadians(time * 3 / 10f) + 1f / 3f) + 1) / 2f));
    }
}
