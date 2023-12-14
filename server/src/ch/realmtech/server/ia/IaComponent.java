package ch.realmtech.server.ia;

import com.artemis.Component;

public class IaComponent extends Component {
    private IaTestTelegraph iaTestTelegraph;
    private IaTestSteerable iaTestSteerable;

    public IaComponent set(IaTestTelegraph iaTestTelegraph, IaTestSteerable iaTestSteerable) {
        this.iaTestTelegraph = iaTestTelegraph;
        this.iaTestSteerable = iaTestSteerable;
        return this;
    }

    public IaTestTelegraph getIaTestAgent() {
        return iaTestTelegraph;
    }

    public IaTestSteerable getIaTestSteerable() {
        return iaTestSteerable;
    }
}
