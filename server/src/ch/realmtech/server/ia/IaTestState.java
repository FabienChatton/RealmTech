package ch.realmtech.server.ia;

import ch.realmtech.server.ecs.component.Box2dComponent;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.steer.behaviors.Seek;

public enum IaTestState implements State<IaTestTelegraph> {

    FOCUS_PLAYER() {
        @Override
        public boolean onMessage(IaTestTelegraph entity, Telegram telegram) {
            int playerId = (int) telegram.extraInfo;
            if (playerId == -1) return false;

            ComponentMapper<IaComponent> mIa = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(IaComponent.class);
            ComponentMapper<Box2dComponent> mBox2d = entity.getServerContext().getEcsEngineServer().getWorld().getMapper(Box2dComponent.class);

            IaComponent iaComponent = mIa.get(entity.getId());
            Box2dComponent playerBox2dComponent = mBox2d.get(playerId);
            iaComponent.getIaTestSteerable().setSteeringBehavior(new Seek<>(iaComponent.getIaTestSteerable(), new Box2dLocation(playerBox2dComponent.body)));
            return true;
        }
    },
    SLEEP() {

    },
    ;
    public final static int FOCUS_PLAYER_MESSAGE = 1;

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

    @Override
    public boolean onMessage(IaTestTelegraph entity, Telegram telegram) {
        if (telegram.message == FOCUS_PLAYER_MESSAGE) {
            return FOCUS_PLAYER.onMessage(entity, telegram);
        }

        return false;
    }
}
