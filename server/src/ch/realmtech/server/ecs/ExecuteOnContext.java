package ch.realmtech.server.ecs;

import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import com.artemis.World;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ExecuteOnContext {
    void onClient(BiConsumer<SystemsAdminClientForClient, World> onClientRun);
    void onServer(Consumer<World> onServerRun);
    void onCommun(Consumer<World> onCommunRun);
}
