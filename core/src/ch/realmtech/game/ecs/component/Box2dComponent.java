package ch.realmtech.game.ecs.component;

import ch.realmtech.game.ecs.PoolableComponent;
import com.badlogic.gdx.physics.box2d.Body;

public class Box2dComponent implements PoolableComponent {
    public Body body;
    public float widthWorld;
    public float heightWorld;

    public void init(float widthWorld, float heightWorld, Body body) {
        this.widthWorld = widthWorld;
        this.heightWorld = heightWorld;
        this.body = body;
    }

    @Override
    public void reset() {
        if (body != null) {
            body.getWorld().destroyBody(body);
        }
        widthWorld = 0;
        heightWorld = 0;
    }
}
