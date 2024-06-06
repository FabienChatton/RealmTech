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

    /**
     * @return true if death
     */
    public boolean decrementHeart(int heartToRemove) {
        if (heart - heartToRemove > 0) {
            heart -= heartToRemove;
            return false;
        } else {
            heart = 0;
            return true;
        }
    }

    public void increaseHeart(int heartToRestore) {
        if (heart + heartToRestore > 10) {
            heart = 10;
        } else {
            heart += heartToRestore;
        }
    }
}
