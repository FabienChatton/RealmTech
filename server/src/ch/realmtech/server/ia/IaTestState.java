package ch.realmtech.server.ia;

import ch.realmtech.server.ecs.component.Box2dComponent;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.steer.behaviors.Seek;

public enum IaTestState implements State<IaTestTelegraph> {

    FOCUS_PLAYER(1) {
        @Override
        public void exit(IaTestTelegraph entity) {
            super.exit(entity);
            ComponentMapper<IaComponent> mIa = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(IaComponent.class);
            IaComponent iaComponent = mIa.get(entity.getId());
            iaComponent.getIaTestSteerable().setSteeringBehavior(null);
        }

        @Override
        public boolean onMessage(IaTestTelegraph entity, Telegram telegram) {
            int playerId = (int) telegram.extraInfo;
            if (playerId == -1) return false;

            ComponentMapper<IaComponent> mIa = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(IaComponent.class);
            ComponentMapper<Box2dComponent> mBox2d = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(Box2dComponent.class);

            IaComponent iaComponent = mIa.get(entity.getId());
            Box2dComponent playerBox2dComponent = mBox2d.get(playerId);
            iaComponent.getIaTestSteerable().setSteeringBehavior(new Seek<>(iaComponent.getIaTestSteerable(), new Box2dLocation(playerBox2dComponent.body)));
            entity.getStateMachine().changeState(FOCUS_PLAYER);
            return true;
        }
    },
    SLEEP(0) {
        @Override
        public boolean onMessage(IaTestTelegraph entity, Telegram telegram) {
            entity.getStateMachine().changeState(SLEEP);
            return true;
        }
    },
    ATTACK_COOLDOWN(2) {
        @Override
        public boolean onMessage(IaTestTelegraph entity, Telegram telegram) {
            return true;
        }
    }
    ;
    public final static int SLEEP_MESSAGE = 0;
    public final static int FOCUS_PLAYER_MESSAGE = 1;
    public static final int ATTACK_COOLDOWN_MESSAGE = 2;
    public final int messageId;

    IaTestState(int messageId) {
        this.messageId = messageId;
    }

    @Override
    public void enter(IaTestTelegraph entity) {
        System.out.println("enter");
    }

    @Override
    public void update(IaTestTelegraph entity) {
        System.out.println("update");
    }

    @Override
    public void exit(IaTestTelegraph entity) {
        System.out.println("exit");
    }
}
