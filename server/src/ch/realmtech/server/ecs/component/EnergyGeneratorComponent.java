package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class EnergyGeneratorComponent extends Component {
    private int remainingTickToBurn;

    public int getRemainingTickToBurn() {
        return remainingTickToBurn;
    }

    public EnergyGeneratorComponent set(int remainingTickToBurn) {
        this.remainingTickToBurn = remainingTickToBurn;
        return this;
    }

    public void setRemainingTickToBurn(int remainingTickToBurn) {
        this.remainingTickToBurn = remainingTickToBurn;
    }
}
