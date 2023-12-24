package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class LifeComponent extends Component {
    private int heart;

    public LifeComponent set(int heart) {
        this.heart = heart;
        return this;
    }

    public int getHeart() {
        return heart;
    }
}
