package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class EnergyGeneratorComponent extends Component {
    private int remainingTickToBurn;

    public int getRemainingTickToBurn() {
        return remainingTickToBurn;
    }

    public void setRemainingTickToBurn(int remainingTickToBurn) {
        this.remainingTickToBurn = remainingTickToBurn;
    }
}
