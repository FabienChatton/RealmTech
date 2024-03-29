package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class FixDynamicBox2dComponent extends Component {
    private Vector2 pos;

    public FixDynamicBox2dComponent set(Vector2 pos) {
        this.pos = pos;
        return this;
    }

    public Vector2 getPos() {
        return pos;
    }
}
