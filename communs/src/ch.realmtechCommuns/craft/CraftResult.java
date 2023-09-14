package ch.realmtechCommuns.craft;

import ch.realmtechCommuns.registery.ItemRegisterEntry;

public class CraftResult {
    private final ItemRegisterEntry itemRegisterEntry;
    private final int nombreResult;
    private final int timeToProcess;

    public CraftResult(ItemRegisterEntry itemRegisterEntry, int nombreResult, int timeToProcess) {
        this.itemRegisterEntry = itemRegisterEntry;
        this.nombreResult = nombreResult;
        this.timeToProcess = timeToProcess;
    }

    public ItemRegisterEntry getItemRegisterEntry() {
        return itemRegisterEntry;
    }

    public int getNombreResult() {
        return nombreResult;
    }

    public int getTimeToProcess() {
        return timeToProcess;
    }
}
