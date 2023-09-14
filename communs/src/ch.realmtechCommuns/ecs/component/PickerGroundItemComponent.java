package ch.realmtechCommuns.ecs.component;

import com.artemis.Component;

public class PickerGroundItemComponent extends Component {
    public float magnetRange = 1;

    public void set(float magnetRange) {
        this.magnetRange = magnetRange;
    }
}
