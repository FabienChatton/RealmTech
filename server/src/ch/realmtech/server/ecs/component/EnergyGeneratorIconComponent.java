package ch.realmtech.server.ecs.component;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class EnergyGeneratorIconComponent extends Component {
    @EntityId
    private int iconFireId;

    public EnergyGeneratorIconComponent set(int iconFireId) {
        this.iconFireId = iconFireId;
        return this;
    }

    public int getIconFireId() {
        return iconFireId;
    }
}
