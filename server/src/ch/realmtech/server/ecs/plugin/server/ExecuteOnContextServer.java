package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import com.artemis.World;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExecuteOnContextServer implements ExecuteOnContext {
    private ServerContext serverContext;

    public ExecuteOnContextServer(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    @Override
    public boolean onClientWorld(BiConsumer<SystemsAdminClientForClient, World> onClientRun) {
        // we are on server context, do nothing
        return false;
    }

    @Override
    public boolean onClientContext(Runnable onClientRun) {
        return false;
    }

    @Override
    public boolean onServer(Consumer<ServerContext> onServerRun) {
        onServerRun.accept(serverContext);
        return true;
    }

    @Override
    public boolean onCommun(Consumer<World> onCommunRun) {
        onCommunRun.accept(serverContext.getEcsEngineServer().getWorld());
        return true;
    }
}
