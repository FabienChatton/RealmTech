package ch.realmtech.server.registry;

import ch.realmtech.server.craft.CraftResult;

import java.util.List;
import java.util.Optional;

public abstract class FurnaceCraftShapeless extends FurnaceCraftRecipeEntry {
    private final CraftPatternShapeless craftPatternShapeless;

    public FurnaceCraftShapeless(String name, String itemResultName, int resultNumber, int timeToProcess, String itemRequire) {
        super(name, timeToProcess);
        this.craftPatternShapeless = new CraftPatternShapeless(itemResultName, itemResultName, resultNumber, itemRequire) {
        };
    }

    @Override
    public Optional<CraftResult> craft(List<List<ItemEntry>> items) {
        return craftPatternShapeless.craft(items)
                .map((craftResult) -> new CraftResult(craftResult.getItemResult(), craftResult.getResultNumber(), timeToProcess));
    }

    @Override
    public List<List<ItemEntry>> getRequireItems() {
        return craftPatternShapeless.getRequireItems();
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        craftPatternShapeless.evaluate(rootRegistry);
    }
}
