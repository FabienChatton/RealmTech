package ch.realmtech.server.enemy;

import ch.realmtech.server.ServerContext;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

public class EnemyTelegraph implements Telegraph {
    private final EnemyStateMachine stateMachine;
    private final int id;
    private final ServerContext serverContext;

    public EnemyTelegraph(int id, ServerContext serverContext) {
        this.id = id;
        this.serverContext = serverContext;
        stateMachine = new EnemyStateMachine(this, EnemyState.SLEEP);
    }

    public int getId() {
        return id;
    }

    public ServerContext getServerContext() {
        return serverContext;
    }

    public EnemyStateMachine getStateMachine() {
        return stateMachine;
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return stateMachine.handleMessage(msg);
    }
}
