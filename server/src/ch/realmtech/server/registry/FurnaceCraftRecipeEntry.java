package ch.realmtech.server.registry;

public abstract class FurnaceCraftRecipeEntry extends CraftRecipeEntry {
    protected int timeToProcess;

    public FurnaceCraftRecipeEntry(String name, int timeToProcess) {
        super(name);
        this.timeToProcess = timeToProcess;
    }
}
