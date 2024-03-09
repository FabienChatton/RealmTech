package ch.realmtech.server.ecs.component;

import ch.realmtech.server.divers.FixList;
import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class MouvementComponent extends Component {
    public List<Vector2> oldPoss;
    public byte lastDirection = 0;

    public MouvementComponent() {
        oldPoss = new FixList<>(10);
    }
}
