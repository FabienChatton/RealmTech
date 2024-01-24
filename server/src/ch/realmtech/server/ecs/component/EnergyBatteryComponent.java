package ch.realmtech.server.ecs.component;

import ch.realmtech.server.ecs.system.EnergyManager;
import com.artemis.Component;

public class EnergyBatteryComponent extends Component {
    private long stored;
    private long capacity;

    public EnergyBatteryComponent set(long stored, long capacity) {
        this.stored = stored;
        this.capacity = capacity;
        return this;
    }

    public long getStored() {
        return stored;
    }

    public boolean setStored(long stored) {
        if (stored > capacity || stored < 0) {
            return false;
        } else {
            this.stored = stored;
            return true;
        }
    }

    public boolean addStored(long addStored) {
        if (this.stored + addStored > capacity) {
            return false;
        } else {
            this.stored += addStored;
            return true;
        }
    }

    public boolean removeStored(long removeStored) {
        if (this.stored - removeStored < 0) {
            return false;
        } else {
            this.stored -= removeStored;
            return true;
        }
    }

    public long getCapacity() {
        return capacity;
    }

    public boolean isEnergyBatteryEmitter() {
        return EnergyManager.isEnergyBatteryEmitter(this);
    }

    public boolean isEnergyBatteryReceiver() {
        return EnergyManager.isEnergyBatteryReceiver(this);
    }
}
