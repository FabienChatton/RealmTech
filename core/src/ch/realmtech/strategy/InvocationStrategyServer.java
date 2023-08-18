package ch.realmtech.strategy;

import com.artemis.BaseSystem;

public interface InvocationStrategyServer {
    void registerServerSystem(BaseSystem serverSystem);

    float getDeltaTime();
}
