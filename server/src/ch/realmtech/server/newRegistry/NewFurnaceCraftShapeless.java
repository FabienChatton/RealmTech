package ch.realmtech.server.newRegistry;

import ch.realmtech.server.newCraft.NewCraftResult;

import java.util.List;
import java.util.Optional;

public class NewFurnaceCraftShapeless extends NewFurnaceCraftRecipeEntry {
    private final NewCraftPatternShapeless newCraftPatternShapeless;

    public NewFurnaceCraftShapeless(String name, String itemResultName, int resultNumber, int timeToProcess, String itemRequire) {
        super(name, timeToProcess);
        this.newCraftPatternShapeless = new NewCraftPatternShapeless(itemResultName, itemResultName, resultNumber, itemRequire);
    }

    @Override
    public Optional<NewCraftResult> craft(List<NewItemEntry> items) {
        return newCraftPatternShapeless.craft(items)
                .map((craftResult) -> new NewCraftResult(craftResult.getItemResult(), craftResult.getResultNumber(), timeToProcess));
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        newCraftPatternShapeless.evaluate(rootRegistry);
    }
}
