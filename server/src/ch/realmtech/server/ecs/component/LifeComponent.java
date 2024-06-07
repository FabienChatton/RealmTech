package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class LifeComponent extends Component {
    private int heart;
    private int killerId;

    public LifeComponent set(int heart) {
        this.heart = heart;
        this.killerId = -1;
        return this;
    }

    public int getHeart() {
        return heart;
    }

    /**
     * @return true if death
     */
    public boolean decrementHeart(int heartToRemove, int attackerId) {
        if (heart - heartToRemove > 0) {
            heart -= heartToRemove;
            return false;
        } else {
            heart = 0;
            killerId = attackerId;
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

    public int getKillerId() {
        return killerId;
    }
}
