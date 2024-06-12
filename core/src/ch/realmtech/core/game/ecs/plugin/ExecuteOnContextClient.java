package ch.realmtech.core.game.ecs.plugin;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.mod.ClientContext;
import com.artemis.World;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExecuteOnContextClient implements ExecuteOnContext {
    private ClientContext clientContext;

    public ExecuteOnContextClient(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    public boolean onClientWorld(BiConsumer<SystemsAdminClientForClient, World> onClientRun) {
        clientContext.getWorldOr((getWorld) -> {
            World world = getWorld.getWorld();
            onClientRun.accept(world.getRegistered("systemsAdmin"), world);
        });
        return true;
    }

    @Override
    public boolean onClientContext(Consumer<ClientContext> onClientRun) {
        onClientRun.accept(clientContext);
        return true;
    }

    @Override
    public boolean onServer(BiConsumer<SystemsAdminServer, ServerContext> onServerRun) {
        return false;
    }

    @Override
    public boolean onCommun(BiConsumer<SystemsAdminCommun, World> onCommunRun) {
        clientContext.getWorldOr((getWorld) -> {
            World world = getWorld.getWorld();
            SystemsAdminCommun systemsAdminCommun = world.getRegistered("systemsAdmin");
            onCommunRun.accept(systemsAdminCommun, world);
        });
        return true;
    }
}
