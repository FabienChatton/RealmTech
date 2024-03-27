package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class InvincibilityComponent extends Component {
    private int remainingTick;
    private int totalTick;

    public InvincibilityComponent set(int remainingTick) {
        this.remainingTick = remainingTick;
        this.totalTick = remainingTick;
        return this;
    }

    public int getRemainingTick() {
        return remainingTick;
    }

    public void setRemainingTick(int remainingTick) {
        this.remainingTick = remainingTick;
    }

    public int getTotalTick() {
        return totalTick;
    }
}
