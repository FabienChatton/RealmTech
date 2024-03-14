package ch.realmtech.server.newRegistry;

public abstract class NewEntry {
    private final String name;
    protected NewRegistry<?> parentRegistry;
    private boolean isEvaluated;

    void setParentRegistry(final NewRegistry<?> parentRegistry) {
        this.parentRegistry = parentRegistry;
    }

    public abstract void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate;
    public NewEntry(String name) {
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
