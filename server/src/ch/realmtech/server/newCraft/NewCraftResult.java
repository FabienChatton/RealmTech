package ch.realmtech.server.newCraft;

import ch.realmtech.server.newRegistry.NewItemEntry;

public class NewCraftResult {
    private final NewItemEntry itemResult;
    private final int resultNumber;
    private final int timeToProcess;

    public NewCraftResult(NewItemEntry itemResult, int resultNumber) {
        this(itemResult, resultNumber, 0);
    }

    public NewCraftResult(NewItemEntry itemResult, int resultNumber, int timeToProcess) {
        this.itemResult = itemResult;
        this.resultNumber = resultNumber;
        this.timeToProcess = timeToProcess;
    }

    public NewItemEntry getItemResult() {
        return itemResult;
    }

    public int getResultNumber() {
        return resultNumber;
    }

    public int getTimeToProcess() {
        return timeToProcess;
    }
}
