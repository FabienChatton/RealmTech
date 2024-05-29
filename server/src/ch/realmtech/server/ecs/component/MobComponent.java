package ch.realmtech.server.ecs.component;

import ch.realmtech.server.registry.MobEntry;
import com.artemis.Component;

public class MobComponent extends Component {
    private MobEntry mobEntry;

    public MobComponent set(MobEntry mobEntry) {
        this.mobEntry = mobEntry;
        return this;
    }

    public MobEntry getMobEntry() {
        return mobEntry;
    }
}
