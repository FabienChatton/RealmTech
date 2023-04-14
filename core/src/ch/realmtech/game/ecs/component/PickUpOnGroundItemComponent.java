package ch.realmtech.game.ecs.component;

import com.artemis.Component;

public class PickUpOnGroundItemComponent extends Component {
    public float magnetRange = 1;

    public void set(float magnetRange) {
        this.magnetRange = magnetRange;
    }
}
