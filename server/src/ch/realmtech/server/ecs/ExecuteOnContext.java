package ch.realmtech.server.ecs;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import ch.realmtech.server.mod.ClientContext;
import com.artemis.World;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ExecuteOnContext {
    boolean onClientWorld(BiConsumer<SystemsAdminClientForClient, World> onClientRun);

    boolean onClientContext(Consumer<ClientContext> onClientRun);

    boolean onServer(Consumer<ServerContext> onServerRun);

    boolean onCommun(Consumer<World> onCommunRun);
}
