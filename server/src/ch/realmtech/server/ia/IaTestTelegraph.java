package ch.realmtech.server.ia;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

public class IaTestTelegraph implements Telegraph {
    private IaTestStateMachine stateMachine;
    private final int id;
    private final ServerContext serverContext;

    public IaTestTelegraph(int id, ServerContext serverContext) {
        this.id = id;
        this.serverContext = serverContext;
        stateMachine = new IaTestStateMachine(this, IaTestState.SLEEP);
    }

    public int getId() {
        return id;
    }

    public ServerContext getServerContext() {
        return serverContext;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return stateMachine.handleMessage(msg);
    }
}
