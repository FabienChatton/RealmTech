package ch.realmtech.game.ecs.component;

import com.artemis.Component;

public class PositionComponent extends Component {
    public float x;
    public float y;

    public void set(float worldX, float worldY) {
        this.x = worldX;
        this.y = worldY;
    }
}
