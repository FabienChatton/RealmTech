package ch.realmtech.core.game.ecs.plugin;

import ch.realmtech.server.ecs.ExecuteOnContext;
import ch.realmtech.server.ecs.plugin.SystemsAdminClientForClient;
import com.artemis.World;

import java.util.function.Consumer;

public class ExecuteOnContextClient implements ExecuteOnContext {
    private World world;
    public void initialize(World world) {
        this.world = world;
    }
    @Override
    public void onClient(Consumer<SystemsAdminClientForClient> onClientRun) {
        onClientRun.accept(world.getRegistered("systemsAdmin"));
    }

    @Override
    public void onServer(Consumer<World> onServerRun) {

    }

    @Override
    public void onCommun(Consumer<World> onCommunRun) {
        onCommunRun.accept(world);
    }
}
