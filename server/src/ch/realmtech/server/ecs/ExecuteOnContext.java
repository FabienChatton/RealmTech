package ch.realmtech.server.ecs;

import ch.realmtech.server.ecs.plugin.SystemsAdminClientForClient;
import com.artemis.World;

import java.util.function.Consumer;

public interface ExecuteOnContext {
    void onClient(Consumer<SystemsAdminClientForClient> onClientRun);
    void onServer(Consumer<World> onServerRun);
    void onCommun(Consumer<World> onCommunRun);
}
