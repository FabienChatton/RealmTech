package ch.realmtech.game.ecs.component;

import com.artemis.Component;

public class CellBeingMineComponent extends Component {
    public int currentStep;
    public int step;

    public CellBeingMineComponent set(int currentStep, int step) {
        this.currentStep = currentStep;
        this.step = step;
        return this;
    }
}
