package ch.realmtech.server.ecs.plugin.server;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.plugin.forclient.SystemsAdminClientForClient;
import com.artemis.World;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExecuteOnContextServer implements ExecuteOnContext {
    private World world;

    public void initialize(World world) {
        this.world = world;
    }

    @Override
    public void onClient(BiConsumer<SystemsAdminClientForClient, World> onClientRun) {
        // we are on server context, do nothing
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
