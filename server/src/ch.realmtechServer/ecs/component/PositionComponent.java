package ch.realmtechServer.ecs.component;

import com.artemis.Component;

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
}
