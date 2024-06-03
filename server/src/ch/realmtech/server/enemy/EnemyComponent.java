package ch.realmtech.server.enemy;

import com.artemis.Component;

import java.util.function.IntConsumer;

public class EnemyComponent extends Component {
    private EnemyTelegraph enemyTelegraph;
    private EnemySteerable enemySteerable;
    private IntConsumer updateEnemy;

    @Deprecated
    private byte flag;

    public EnemyComponent set(EnemyTelegraph enemyTelegraph, EnemySteerable enemySteerable, IntConsumer updateEnemy) {
        this.enemyTelegraph = enemyTelegraph;
        this.enemySteerable = enemySteerable;
        this.updateEnemy = updateEnemy;
        return this;
    }

    public EnemyTelegraph getEnemyTelegraph() {
        return enemyTelegraph;
    }

    public EnemySteerable getEnemySteerable() {
        return enemySteerable;
    }

    public IntConsumer getUpdateEnemy() {
        return updateEnemy;
    }
}
