package ch.realmtech.server.registry;

import ch.realmtech.server.craft.CraftResult;
import ch.realmtech.server.craft.PatternArgs;

import java.util.*;

/**
 * Shape pattern are valide craft when the shape is meet this the defined items. <br />
 * For exemple:
 * <pre>
 * +------------+
 * | +-------+  |
 * | | plank |  |
 * | +-------+  |
 * | | plank |  |
 * | +-------+  |
 * |  => stick  |
 * +------------+
 * </pre>
 * <pre>
 * +--------------------------------+
 * | +-------+-------+              |
 * | | plank |       |              |
 * | +-------+-------+   valide     |
 * | | plank |       |              |
 * | +-------+-------+              |
 * |                                |
 * | +-------+-------+              |
 * | |       | plank |              |
 * | +-------+-------+   valide     |
 * | |       | plank |              |
 * | +-------+-------+              |
 * |                                |
 * | +-------+-------+              |
 * | |       | plank |              |
 * | +-------+-------+   invalide,  |
 * | | plank | plank |   shape      |
 * | +-------+-------+   mismatch   |
 * +--------------------------------+
 * </pre>
 */
public abstract class CraftPatternShape extends CraftRecipeEntry {
    private final String itemResultName;
    private ItemEntry itemResult;
    private final int resultNumber;
    private final String[][] craftPattern2dName;
    private List<List<ItemEntry>> craftPattern2d;

    /**
     * Create a pattern shape.
     *
     * @param name           Name of the entry.
     * @param itemResultFqrn Item result fqrn.
     * @param resultNumber   Number of result items.
     * @param pattern        The pattern of the craft. {@link PatternArgs more info about pattern}
     * @param patternArg     Pattern arg. One pattern arg require.
     * @param patternArgs    More pattern arg.
     */
    public CraftPatternShape(String name, String itemResultFqrn, int resultNumber, char[][] pattern, PatternArgs patternArg, PatternArgs... patternArgs) {
        super(name);
        this.itemResultName = itemResultFqrn;
        this.resultNumber = resultNumber;
        PatternArgs[] args = new PatternArgs[1 + patternArgs.length];
        args[0] = patternArg;
        System.arraycopy(patternArgs, 0, args, 1, patternArgs.length);
        this.craftPattern2dName = getCraftPattern2dName(pattern, args);
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
        boolean valide = false;

        loop:
        for (int testI = 0; testI < items.size(); testI++) {

            testItems:
            for (int testJ = 0; testJ < items.get(testI).size(); testJ++) {
                for (int expectedI = 0; expectedI < craftPattern2d.size(); expectedI++) {
                    if (expectedI + testI > items.get(expectedI).size()) continue testItems;
                    for (int expectedJ = 0; expectedJ < craftPattern2d.get(expectedI).size(); expectedJ++) {
                        if (expectedJ + testJ >= items.get(expectedI).size()) continue testItems;
                        if (expectedI + testI >= items.size()) continue;
                        if (craftPattern2d.get(expectedI).get(expectedJ) != items.get(expectedI + testI).get(expectedJ + testJ)) {
                            // craft invalide
                            if (!valide) {
                                // no items have already been matched
                                // continue to find a valide craft in the grid
                                continue testItems;
                            } else {
                                // an item have been matched
                                // and this match is invalide
                                // break the loop, the craft is invalide
                                break loop;
                            }
                        } else {
                            // craft valide
                            valide = true;
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
            if (symbol == patternArg.symbol()) {
                return patternArg.itemFqrn();
            }
        }
        return null;
    }
}
