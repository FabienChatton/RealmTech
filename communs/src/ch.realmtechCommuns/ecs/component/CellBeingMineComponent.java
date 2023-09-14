package ch.realmtechCommuns.ecs.component;

import com.artemis.Component;

public class CellBeingMineComponent extends Component {
    public final static int INFINITE_MINE = -1;
    public int currentStep;
    public int step;

    public CellBeingMineComponent set(int currentStep, int step) {
        this.currentStep = currentStep;
        this.step = step;
        return this;
    }
}
