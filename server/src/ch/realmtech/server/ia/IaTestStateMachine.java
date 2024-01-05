package ch.realmtech.server.ia;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;

public class IaTestStateMachine extends DefaultStateMachine<IaTestTelegraph, IaTestState> {
    public IaTestStateMachine() {
        super();
    }

    public IaTestStateMachine(IaTestTelegraph owner) {
        super(owner);
    }

    public IaTestStateMachine(IaTestTelegraph owner, IaTestState initialState) {
        super(owner, initialState);
    }

    public IaTestStateMachine(IaTestTelegraph owner, IaTestState initialState, IaTestState globalState) {
        super(owner, initialState, globalState);
    }

    @Override
    public boolean handleMessage(Telegram telegram) {
        return super.handleMessage(telegram);
    }
}
