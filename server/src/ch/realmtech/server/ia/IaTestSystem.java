package ch.realmtech.server.ia;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.physics.box2d.World;

@All(IaComponent.class)
public class IaTestSystem extends IteratingSystem {
    private ComponentMapper<IaComponent> mIa;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Override
    protected void process(int entityId) {
        IaComponent iaComponent = mIa.get(entityId);
        iaComponent.getIaTestSteerable().update(world.getDelta());
    }
}
