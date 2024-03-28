package ch.realmtech.server.enemy;

import com.artemis.Component;

public class EnemyComponent extends Component {
    private EnemyTelegraph enemyTelegraph;
    private EnemySteerable enemySteerable;

    public EnemyComponent set(EnemyTelegraph enemyTelegraph, EnemySteerable enemySteerable) {
        this.enemyTelegraph = enemyTelegraph;
        this.enemySteerable = enemySteerable;
        return this;
    }

    public EnemyTelegraph getIaTestAgent() {
        return enemyTelegraph;
    }

    public EnemySteerable getIaTestSteerable() {
        return enemySteerable;
    }
}
