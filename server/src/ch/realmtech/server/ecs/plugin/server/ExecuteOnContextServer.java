package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.mod.ClientContext;
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
    public boolean onClientContext(Consumer<ClientContext> onClientRun) {
        return false;
    }

    @Override
    public boolean onServer(BiConsumer<SystemsAdminServer, ServerContext> onServerRun) {
        SystemsAdminServer systemsAdminServer = serverContext.getSystemsAdminServer();
        onServerRun.accept(systemsAdminServer, serverContext);
        return true;
    }

    @Override
    public boolean onCommun(BiConsumer<SystemsAdminCommun, World> onCommunRun) {
        World world = serverContext.getEcsEngineServer().getWorld();
        SystemsAdminCommun systemsAdminCommun = world.getRegistered("systemsAdmin");
        onCommunRun.accept(systemsAdminCommun, world);
        return true;
    }
}
