package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class EnemyAttackCooldownComponent extends Component {
    private int remainingTick;
    private int totalTick;
    private Runnable onEnd;

    public EnemyAttackCooldownComponent set(int remainingTick, Runnable onEnd) {
        this.remainingTick = remainingTick;
        this.totalTick = remainingTick;
        this.onEnd = onEnd;
        return this;
    }

    public int getRemainingTick() {
        return remainingTick;
    }

    public void setRemainingTick(int remainingTick) {
        this.remainingTick = remainingTick;
    }

    public Runnable getOnEnd() {
        return onEnd;
    }

    public int getTotalTick() {
        return totalTick;
    }
}
