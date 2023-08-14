package ch.realmtech.game.ecs;

import com.artemis.BaseSystem;

public interface InvocationStrategyServer {
    void registerServerSystem(BaseSystem serverSystem);
}
