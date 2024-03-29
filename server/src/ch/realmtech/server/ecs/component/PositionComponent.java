package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class PositionComponent extends Component {
    public float x;
    public float y;

    public void set(float worldX, float worldY) {
        this.x = worldX;
        this.y = worldY;
    }

    public void set(Box2dComponent box2dComponent, float worldX, float worldY) {
        set(worldX, worldY);
        box2dComponent.body.setTransform(worldX + box2dComponent.widthWorld / 2, worldY + box2dComponent.heightWorld / 2, box2dComponent.body.getAngle());
    }

    public Vector2 toVector2() {
        return new Vector2(x, y);
    }

    @Override
    public String toString() {
        return String.format("%f %f", x, y);
    }
}
