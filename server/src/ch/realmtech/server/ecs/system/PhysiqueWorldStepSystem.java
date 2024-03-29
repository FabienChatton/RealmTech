package ch.realmtech.server.ecs.system;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.World;

public class PhysiqueWorldStepSystem extends BaseSystem {
    @Wire(name = "physicWorld")
    private World physicWorld;

    @Override
    protected void processSystem() {
        physicWorld.step(1f / 60f, 6, 2);
    }
}
