package ch.realmtech.server.ia;

import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

public class IaTestTelegraph implements Telegraph {
    private StateMachine<IaTestTelegraph, IaTestState> stateMachine;

    @Override
    public boolean handleMessage(Telegram msg) {
        return stateMachine.handleMessage(msg);
    }
}
