package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class EnergyBattery extends Component {
    private long stored;
    private long capacity;

    public EnergyBattery set(long stored, long capacity) {
        this.stored = stored;
        this.capacity = capacity;
        return this;
    }

    public long getStored() {
        return stored;
    }

    public long getCapacity() {
        return capacity;
    }
}
