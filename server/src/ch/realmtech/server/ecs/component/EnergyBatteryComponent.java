package ch.realmtech.server.ecs.component;

import ch.realmtech.server.ecs.system.EnergyManager;
import com.artemis.Component;

public class EnergyBatteryComponent extends Component {
    private long stored;
    private long capacity;
    private byte energyBatteryRole;

    public EnergyBatteryComponent set(long stored, long capacity, EnergyBatteryRole energyBatteryRole) {
        this.stored = stored;
        this.capacity = capacity;
        this.energyBatteryRole = energyBatteryRole.roleByte;
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

    public byte getEnergyBatteryRole() {
        return energyBatteryRole;
    }

    public boolean isEnergyBatteryEmitter() {
        return EnergyManager.isEnergyBatteryEmitter(this);
    }

    public boolean isEnergyBatteryReceiver() {
        return EnergyManager.isEnergyBatteryReceiver(this);
    }

    public boolean isFull() {
        return EnergyManager.isFull(this);
    }


    public enum EnergyBatteryRole {
        EMITTER_ONLY((byte) 0x1),
        RECEIVER_ONLY((byte) 0x2),
        EMITTER_RECEIVER((byte) 0x3);
        private final byte roleByte;

        EnergyBatteryRole(byte roleByte) {
            this.roleByte = roleByte;
        }

        public byte getRoleByte() {
            return roleByte;
        }
    }
}
