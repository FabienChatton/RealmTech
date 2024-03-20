package ch.realmtech.server.registry;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.craft.PatternArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class CraftPatternShape extends CraftRecipeEntry {
    private final String itemResultName;
    private ItemEntry itemResult;
    private final int resultNumber;
    private final String[][] craftPattern2dName;
    private List<List<ItemEntry>> craftPattern2d;

    public CraftPatternShape(String name, String itemResultName, int resultNumber, char[][] pattern, PatternArgs newPatternArg, PatternArgs... newPatternArgs) {
        super(name);
        this.itemResultName = itemResultName;
        this.resultNumber = resultNumber;
        PatternArgs[] patternArgs = new PatternArgs[1 + newPatternArgs.length];
        patternArgs[0] = newPatternArg;
        System.arraycopy(newPatternArgs, 0, patternArgs, 1, newPatternArgs.length);
        this.craftPattern2dName = getCraftPattern2dName(pattern, patternArgs);
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        craftPattern2d = new ArrayList<>(craftPattern2dName.length);
        for (int i = 0; i < craftPattern2dName.length; i++) {
            craftPattern2d.add(i, new ArrayList<>(craftPattern2dName[i].length));
            for (int j = 0; j < craftPattern2dName[i].length; j++) {
                String itemName = craftPattern2dName[i][j];
                if (itemName != null) {
                    ItemEntry itemEntry = RegistryUtils.evaluateSafe(rootRegistry, itemName, ItemEntry.class);
                    craftPattern2d.get(i).add(j, itemEntry);
                } else {
                    craftPattern2d.get(i).add(j, null);
                }
            }
        }
        itemResult = RegistryUtils.evaluateSafe(rootRegistry, itemResultName, ItemEntry.class);
    }

    @Override
    public Optional<CraftResult> craft(List<List<ItemEntry>> items) {
        if (items.equals(craftPattern2d)) {
            return Optional.of(new CraftResult(itemResult, resultNumber));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<List<ItemEntry>> getRequireItems() {
        return craftPattern2d;
    }

    private String[][] getCraftPattern2dName(char[][] pattern, PatternArgs[] patternArgs) {
        String[][] ret = new String[pattern.length][];
        for (int i = 0; i < pattern.length; i++) {
            ret[i] = new String[pattern[i].length];
            for (int j = 0; j < pattern[i].length; j++) {
                ret[i][j] = getItemName(pattern[i][j], patternArgs);
            }
        }
        return ret;
    }

    private String getItemName(char symbol, PatternArgs[] patternArgs) {
        for (PatternArgs patternArg : patternArgs) {
            if (symbol == patternArg.getSymbol()) {
                return patternArg.getItemName();
            }
        }
        return null;
    }
}
