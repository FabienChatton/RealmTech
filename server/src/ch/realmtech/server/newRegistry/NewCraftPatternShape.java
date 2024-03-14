package ch.realmtech.server.newRegistry;

import ch.realmtech.server.newCraft.NewCraftResult;
import ch.realmtech.server.newCraft.NewPatternArgs;

import java.util.List;
import java.util.Optional;

public class NewCraftPatternShape extends NewCraftRecipeEntry {
    private final String itemResultName;
    private final int resultNumber;
    private final String[][] craftPattern2dName;
    private NewItemEntry[][] craftPattern2d;

    public NewCraftPatternShape(String name, String itemResultName, int resultNumber, char[][] pattern, NewPatternArgs newPatternArg, NewPatternArgs... newPatternArgs) {
        super(name);
        this.itemResultName = itemResultName;
        this.resultNumber = resultNumber;
        NewPatternArgs[] patternArgs = new NewPatternArgs[1 + newPatternArgs.length];
        patternArgs[0] = newPatternArg;
        System.arraycopy(newPatternArgs, 0, patternArgs, 1, newPatternArgs.length);
        this.craftPattern2dName = getCraftPattern2dName(pattern, patternArgs);
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        craftPattern2d = new NewItemEntry[craftPattern2dName.length][];
        for (int i = 0; i < craftPattern2dName.length; i++) {
            craftPattern2d[i] = new NewItemEntry[craftPattern2dName[i].length];
            for (int j = 0; j < craftPattern2dName[i].length; j++) {
                String itemName = craftPattern2dName[i][j];
                if (itemName != null) {
                    NewItemEntry itemEntry = RegistryUtils.evaluateSafe(rootRegistry, itemName, NewItemEntry.class);
                    craftPattern2d[i][j] = itemEntry;
                }
            }
        }
    }

    @Override
    public Optional<NewCraftResult> craft(List<NewItemEntry> items) {
        return Optional.empty();
    }

    private String[][] getCraftPattern2dName(char[][] pattern, NewPatternArgs[] patternArgs) {
        String[][] ret = new String[pattern.length][];
        for (int i = 0; i < pattern.length; i++) {
            ret[i] = new String[pattern[i].length];
            for (int j = 0; j < pattern[i].length; j++) {
                ret[i][j] = getItemName(pattern[i][j], patternArgs);
            }
        }
        return ret;
    }

    private String getItemName(char symbol, NewPatternArgs[] patternArgs) {
        for (NewPatternArgs patternArg : patternArgs) {
            if (symbol == patternArg.getSymbol()) {
                return patternArg.getItemName();
            }
        }
        return null;
    }
}
