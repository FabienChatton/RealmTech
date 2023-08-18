package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;

public class PhysiqueWorldStepSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;

    @Override
    protected void processSystem() {
        context.getEcsEngine().physicWorld.step(0.025f, 6, 2);
    }
}
