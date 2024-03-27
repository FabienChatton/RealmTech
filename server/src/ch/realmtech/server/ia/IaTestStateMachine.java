package ch.realmtech.server.ia;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.Telegram;

import static ch.realmtech.server.ia.IaTestState.*;

public class IaTestStateMachine extends DefaultStateMachine<IaTestTelegraph, IaTestState> {
    public IaTestStateMachine(IaTestTelegraph owner, IaTestState initialState) {
        super(owner, initialState);
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        return dispatch(owner, telegram);
    }

    public boolean dispatch(IaTestTelegraph entity, Telegram telegram) {
        if (currentState.messageId == telegram.message) return false;
        return switch (telegram.message) {
            case SLEEP_MESSAGE -> SLEEP.onMessage(entity, telegram);
            case FOCUS_PLAYER_MESSAGE -> FOCUS_PLAYER.onMessage(entity, telegram);
            case ATTACK_COOLDOWN_MESSAGE -> ATTACK_COOLDOWN.onMessage(entity, telegram);
            default -> false;
        };
    }
}
