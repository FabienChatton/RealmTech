package ch.realmtech.server.enemy;

import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.FixDynamicBox2dComponent;
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
            enemyComponent.getEnemySteerable().setSteeringBehavior(null);
        }

        @Override
        public boolean onMessage(EnemyTelegraph entity, Telegram telegram) {
            super.onMessage(entity, telegram);
            int playerId = (int) telegram.extraInfo;
            if (playerId == -1) return false;

            ComponentMapper<EnemyComponent> mEnemy = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(EnemyComponent.class);
            ComponentMapper<Box2dComponent> mBox2d = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(Box2dComponent.class);

            EnemyComponent enemyComponent = mEnemy.get(entity.getId());
            Box2dComponent playerBox2dComponent = mBox2d.get(playerId);
            enemyComponent.getEnemySteerable().setSteeringBehavior(new Seek<>(enemyComponent.getEnemySteerable(), new Box2dLocation(playerBox2dComponent.body)));
            return true;
        }
    },
    SLEEP(0) {
        @Override
        public boolean onMessage(EnemyTelegraph entity, Telegram telegram) {
            super.onMessage(entity, telegram);
            ComponentMapper<EnemyComponent> mEnemy = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(EnemyComponent.class);
            EnemyComponent enemyComponent = mEnemy.get(entity.getId());
            enemyComponent.getEnemySteerable().setSteeringBehavior(null);
            entity.getStateMachine().changeState(SLEEP);
            return true;
        }
    },
    ATTACK_COOLDOWN(2) {
        @Override
        public boolean onMessage(EnemyTelegraph entity, Telegram telegram) {
            super.onMessage(entity, telegram);
            ComponentMapper<EnemyComponent> mEnemy = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(EnemyComponent.class);
            EnemyComponent enemyComponent = mEnemy.get(entity.getId());
            enemyComponent.getEnemySteerable().setSteeringBehavior(null);
            return true;
        }

        @Override
        public void enter(EnemyTelegraph entity) {
            int mobId = entity.getId();
            ComponentMapper<Box2dComponent> mBox2d = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(Box2dComponent.class);
            entity.getServerContext().getEcsEngineServer().getWorld().edit(mobId).create(FixDynamicBox2dComponent.class).set(mBox2d.get(mobId).body.getPosition());
        }

        @Override
        public void exit(EnemyTelegraph entity) {
            int mobId = entity.getId();
            entity.getServerContext().getEcsEngineServer().getWorld().edit(mobId).remove(FixDynamicBox2dComponent.class);
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
    }

    @Override
    public void update(EnemyTelegraph entity) {
    }

    @Override
    public void exit(EnemyTelegraph entity) {
    }

    @Override
    public boolean onMessage(EnemyTelegraph entity, Telegram telegram) {
        entity.getStateMachine().changeState(this);
        return false;
    }


}
