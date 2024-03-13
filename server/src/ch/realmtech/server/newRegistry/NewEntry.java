package ch.realmtech.server.newRegistry;

public abstract class NewEntry {
    private final String name;
    protected NewRegistry<?> parentRegistry;
    private boolean isEvaluated;

    void setParentRegistry(final NewRegistry<?> parentRegistry) {
        this.parentRegistry = parentRegistry;
    }

    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        isEvaluated = true;
    }
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

    @Override
    public String toString() {
        return parentRegistry != null ? toFqrn() : name;
    }
}
