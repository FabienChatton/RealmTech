package ch.realmtech.server.ecs.system;


import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.FixDynamicBox2dComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

@All({FixDynamicBox2dComponent.class, Box2dComponent.class})
public class FixDynamicBodySystem extends IteratingSystem {
    private ComponentMapper<FixDynamicBox2dComponent> mFixBody;
    private ComponentMapper<Box2dComponent> mBox2d;

    @Override
    protected void process(int entityId) {
        Vector2 fixPos = mFixBody.get(entityId).getPos();
        Box2dComponent box2dComponent = mBox2d.get(entityId);
        box2dComponent.body.setTransform(fixPos, 0);
    }
}
