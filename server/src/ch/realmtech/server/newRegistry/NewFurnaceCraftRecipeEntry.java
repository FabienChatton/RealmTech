package ch.realmtech.server.newRegistry;

public abstract class NewFurnaceCraftRecipeEntry extends NewCraftRecipeEntry {
    protected int timeToProcess;

    public NewFurnaceCraftRecipeEntry(String name, int timeToProcess) {
        super(name);
        this.timeToProcess = timeToProcess;
    }
}
