package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class EnergyGeneratorComponent extends Component {
    private int remainingTickToBurn;
    private int lastRemainingTickToBurn;

    public int getRemainingTickToBurn() {
        return remainingTickToBurn;
    }

    public int getLastRemainingTickToBurn() {
        return lastRemainingTickToBurn;
    }

    public EnergyGeneratorComponent set(int remainingTickToBurn, int lastRemainingTickToBurn) {
        this.remainingTickToBurn = remainingTickToBurn;
        this.lastRemainingTickToBurn = lastRemainingTickToBurn;
        return this;
    }

    public void newRemainingTickToBurn(int remainingTickToBurn) {
        this.remainingTickToBurn = remainingTickToBurn;
        this.lastRemainingTickToBurn = remainingTickToBurn;
    }

    public void setRemainingTickToBurn(int remainingTickToBurn) {
        this.remainingTickToBurn = remainingTickToBurn;
    }
}
