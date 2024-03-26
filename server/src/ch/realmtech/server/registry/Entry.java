package ch.realmtech.server.registry;

import ch.realmtech.server.mod.EvaluateAfter;
import ch.realmtech.server.mod.EvaluateBefore;

/**
 * Store the content of RealmTech. Extends this class to a entry to a registry.
 * Entry are the content of RealmTech. A entry need to have a parent registry to work properly.
 * Every entry has a name.
 */
public abstract class Entry {
    private final String name;
    protected Registry<?> parentRegistry;
    private boolean isEvaluated;

    void setParentRegistry(final Registry<?> parentRegistry) {
        this.parentRegistry = parentRegistry;
    }

    /**
     * Evaluate this registry. Use this methode to make static reference instance of fqrn reference,
     * this will reduce the number of runtime error. The goal of evaluation methode is to create
     * static reference for dependency of this entry to reduce the number of runtime error during the game phase.
     * You can specify if the evaluation occurre before or after another entry with the
     * {@link EvaluateBefore} or {@link EvaluateAfter} annotation.
     * <pre>
     * +-----------------+
     * | Initialization  | add all registry to root registry
     * |       |         |
     * |      \|/        |
     * |   evaluation    | access to all registry
     * +-----------------+
     * </pre>
     *
     * @param rootRegistry The Root registry with all the registry and entry initialized.
     * @throws InvalideEvaluate If the evaluation is fail. Terminate the mod loading.
     */
    public abstract void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate;

    /**
     * Get the name of the entry.
     * @return The name of the entry.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the fqrn of the entry.
     * @return The fqrn of the entry or throws a exception if the entry don't have a parent registry.
     */
    public String toFqrn() {
        return parentRegistry.toFqrn() + "." + name;
    }

    /**
     * Get the id of the entry. The id is the binary notation for an entry. The id will be used for the serialization
     * of this entry.
     * @return The id of the entry.
     */
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
