package ch.realmtech.strategy;

import com.artemis.BaseSystem;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;

public class WorldConfigurationBuilderServer {
    private final WorldConfigurationBuilder worldConfigurationBuilder;
    private final InvocationStrategyServer invocationStrategyServer;

    public WorldConfigurationBuilderServer(final InvocationStrategyServer invocationStrategyServer) {
        this.worldConfigurationBuilder = new WorldConfigurationBuilder();
        this.invocationStrategyServer = invocationStrategyServer;
    }

    public WorldConfigurationBuilderServer dependsOn(Class type) {
        worldConfigurationBuilder.dependsOn(type);
        return this;
    }

    public WorldConfigurationBuilderServer withClient(BaseSystem clientSystem) {
        worldConfigurationBuilder.with(clientSystem);
        return this;
    }

    public WorldConfigurationBuilderServer withServer(BaseSystem serverSystem) {
        worldConfigurationBuilder.with(serverSystem);
        invocationStrategyServer.registerServerSystem(serverSystem);
        return this;
    }

    public WorldConfiguration build() {
        return worldConfigurationBuilder.build();
    }
}
