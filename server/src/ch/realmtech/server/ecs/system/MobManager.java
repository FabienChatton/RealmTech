package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.Box2dComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;

public class MobManager extends Manager {
    @Wire(name = "physicWorld")
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    private ComponentMapper<Box2dComponent> mBox2d;

    public void destroyMob(int mobId) {
        Box2dComponent box2dComponent = mBox2d.get(mobId);
        physicWorld.destroyBody(box2dComponent.body);
        world.delete(mobId);
    }
}
