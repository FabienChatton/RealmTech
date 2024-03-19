package ch.realmtech.server.craft;

import ch.realmtech.server.registry.ItemEntry;

public class CraftResult {
    private final ItemEntry itemResult;
    private final int resultNumber;
    private final int timeToProcess;

    public CraftResult(ItemEntry itemResult, int resultNumber) {
        this(itemResult, resultNumber, 0);
    }

    public CraftResult(ItemEntry itemResult, int resultNumber, int timeToProcess) {
        this.itemResult = itemResult;
        this.resultNumber = resultNumber;
        this.timeToProcess = timeToProcess;
    }

    public ItemEntry getItemResult() {
        return itemResult;
    }

    public int getResultNumber() {
        return resultNumber;
    }

    public int getTimeToProcess() {
        return timeToProcess;
    }
}
