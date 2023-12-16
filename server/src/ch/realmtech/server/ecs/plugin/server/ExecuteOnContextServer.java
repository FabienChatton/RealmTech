package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.plugin.SystemsAdminClientForClient;
import com.artemis.World;

import java.util.function.Consumer;

public class ExecuteOnContextServer implements ExecuteOnContext {
    private World world;

    public void initialize(World world) {
        this.world = world;
    }

    @Override
    public void onClient(Consumer<SystemsAdminClientForClient> onClientRun) {

    }

    @Override
    public void onServer(Consumer<World> onServerRun) {
        onServerRun.accept(world);
    }

    @Override
    public void onCommun(Consumer<World> onCommunRun) {
        onCommunRun.accept(world);
    }
}
