package ch.realmtech.server.enemy;

import ch.realmtech.server.ecs.component.Box2dComponent;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.steer.behaviors.Seek;

public enum EnemyState implements State<EnemyTelegraph> {

    FOCUS_PLAYER(1) {
        @Override
        public void exit(EnemyTelegraph entity) {
            super.exit(entity);
            ComponentMapper<EnemyComponent> mEnemy = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(EnemyComponent.class);
            EnemyComponent enemyComponent = mEnemy.get(entity.getId());
            enemyComponent.getIaTestSteerable().setSteeringBehavior(null);
        }

        @Override
        public boolean onMessage(EnemyTelegraph entity, Telegram telegram) {
            int playerId = (int) telegram.extraInfo;
            if (playerId == -1) return false;

            ComponentMapper<EnemyComponent> mEnemy = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(EnemyComponent.class);
            ComponentMapper<Box2dComponent> mBox2d = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(Box2dComponent.class);

            EnemyComponent enemyComponent = mEnemy.get(entity.getId());
            Box2dComponent playerBox2dComponent = mBox2d.get(playerId);
            enemyComponent.getIaTestSteerable().setSteeringBehavior(new Seek<>(enemyComponent.getIaTestSteerable(), new Box2dLocation(playerBox2dComponent.body)));
            entity.getStateMachine().changeState(FOCUS_PLAYER);
            return true;
        }
    },
    SLEEP(0) {
        @Override
        public boolean onMessage(EnemyTelegraph entity, Telegram telegram) {
            ComponentMapper<EnemyComponent> mEnemy = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(EnemyComponent.class);
            EnemyComponent enemyComponent = mEnemy.get(entity.getId());
            enemyComponent.getIaTestSteerable().setSteeringBehavior(null);
            entity.getStateMachine().changeState(SLEEP);
            return true;
        }
    },
    ATTACK_COOLDOWN(2) {
        @Override
        public boolean onMessage(EnemyTelegraph entity, Telegram telegram) {
            return true;
        }
    }
    ;
    public final static int SLEEP_MESSAGE = 0;
    public final static int FOCUS_PLAYER_MESSAGE = 1;
    public static final int ATTACK_COOLDOWN_MESSAGE = 2;
    public final int messageId;

    EnemyState(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public void enter(EnemyTelegraph entity) {
        System.out.println("enter");
    }

    @Override
    public void update(EnemyTelegraph entity) {
        System.out.println("update");
    }

    @Override
    public void exit(EnemyTelegraph entity) {
        System.out.println("exit");
    }
}
