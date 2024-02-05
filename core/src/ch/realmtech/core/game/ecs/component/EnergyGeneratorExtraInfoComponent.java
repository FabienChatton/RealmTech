package ch.realmtech.core.game.ecs.component;

import com.artemis.Component;

public class EnergyGeneratorExtraInfoComponent extends Component {
    private int lastRemainingTickToBurn;

    public EnergyGeneratorExtraInfoComponent set(int lastRemainingTickToBurn) {
        this.lastRemainingTickToBurn = lastRemainingTickToBurn;
        return this;
    }

    public int getLastRemainingTickToBurn() {
        return lastRemainingTickToBurn;
    }
}
