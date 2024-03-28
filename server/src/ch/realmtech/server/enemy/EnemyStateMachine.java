package ch.realmtech.server.enemy;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.msg.Telegram;

public class EnemyStateMachine extends DefaultStateMachine<EnemyTelegraph, EnemyState> {
    public EnemyStateMachine(EnemyTelegraph owner, EnemyState initialState) {
        super(owner, initialState);
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        return dispatch(owner, telegram);
    }

    public boolean dispatch(EnemyTelegraph entity, Telegram telegram) {
        if (currentState.messageId == telegram.message) return false;
        return switch (telegram.message) {
            case EnemyState.SLEEP_MESSAGE -> EnemyState.SLEEP.onMessage(entity, telegram);
            case EnemyState.FOCUS_PLAYER_MESSAGE -> EnemyState.FOCUS_PLAYER.onMessage(entity, telegram);
            case EnemyState.ATTACK_COOLDOWN_MESSAGE -> EnemyState.ATTACK_COOLDOWN.onMessage(entity, telegram);
            default -> false;
        };
    }
}
