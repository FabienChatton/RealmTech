package ch.realmtech.server.ecs.component;

import box2dLight.Light;
import com.artemis.Component;

public class LightComponent extends Component {
    private Light light;

    public LightComponent set(Light light) {
        this.light = light;
        return this;
    }

    public Light getLight() {
        return light;
    }
}
