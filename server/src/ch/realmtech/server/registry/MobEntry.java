package ch.realmtech.server.registry;

import ch.realmtech.server.enemy.MobBehavior;

public abstract class MobEntry extends Entry {
    private final MobBehavior mobBehavior;

    public MobEntry(String name) {
        super(name);
        mobBehavior = initializeMobBehavior();
    }

    protected abstract MobBehavior initializeMobBehavior();

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        mobBehavior.evaluate(rootRegistry);
    }

    public MobBehavior getMobBehavior() {
        return mobBehavior;
    }
}
