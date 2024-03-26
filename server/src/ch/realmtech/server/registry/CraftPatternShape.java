package ch.realmtech.server.registry;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.craft.PatternArgs;

import java.util.*;

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
        if (items.stream().flatMap(Collection::stream).filter(Objects::nonNull).count() != craftPattern2d.stream().flatMap(Collection::stream).filter(Objects::nonNull).count())
            return Optional.empty();
        if (items.size() < craftPattern2d.size()) return Optional.empty();
        for (int testI = 0; testI < items.size(); testI++) {

            testItems:
            for (int testJ = 0; testJ < items.get(testI).size(); testJ++) {
                for (int expectedI = 0; expectedI < craftPattern2d.size(); expectedI++) {
                    if (expectedI + testI > items.get(expectedI).size()) continue testItems;
                    for (int expectedJ = 0; expectedJ < craftPattern2d.get(expectedI).size(); expectedJ++) {
                        if (expectedJ + testJ >= items.get(expectedI).size()) continue testItems;
                        if (craftPattern2d.get(expectedI).get(expectedJ) != items.get(expectedI + testI).get(expectedJ + testJ)) {
                            continue testItems;
                        }
                    }
                }
                return Optional.of(new CraftResult(itemResult, resultNumber));
            }
        }
        return Optional.empty();
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
