package ch.realmtech.server.registry;

public abstract class Entry {
    private final String name;
    protected Registry<?> parentRegistry;
    private boolean isEvaluated;

    void setParentRegistry(final Registry<?> parentRegistry) {
        this.parentRegistry = parentRegistry;
    }

    public abstract void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate;

    public Entry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toFqrn() {
        return parentRegistry.toFqrn() + "." + name;
    }

    public int getId() {
        return toFqrn().hashCode();
    }

    public boolean isEvaluated() {
        return isEvaluated;
    }

    public void setEvaluated(boolean evaluated) {
        isEvaluated = evaluated;
    }

    @Override
    public String toString() {
        return parentRegistry != null ? toFqrn() : name;
    }
}
