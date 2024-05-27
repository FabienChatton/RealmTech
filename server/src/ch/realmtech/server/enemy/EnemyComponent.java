package ch.realmtech.server.enemy;

import com.artemis.Component;

import java.util.function.IntConsumer;

public class EnemyComponent extends Component {
    private EnemyTelegraph enemyTelegraph;
    private EnemySteerable enemySteerable;
    private IntConsumer updateEnemy;
    private byte flag;
    public final static byte PASSIVE_MOB_FLAG = 1;
    public final static byte ZOMBIE_FLAG = 2;
    public final static byte ITEM_FLAG = 3;

    public EnemyComponent set(EnemyTelegraph enemyTelegraph, EnemySteerable enemySteerable, IntConsumer updateEnemy, byte flag) {
        this.enemyTelegraph = enemyTelegraph;
        this.enemySteerable = enemySteerable;
        this.updateEnemy = updateEnemy;
        this.flag = flag;
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

    public byte getFlag() {
        return flag;
    }
}
