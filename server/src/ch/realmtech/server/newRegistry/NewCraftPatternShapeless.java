package ch.realmtech.server.newRegistry;

import ch.realmtech.server.newCraft.NewCraftResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class NewCraftPatternShapeless extends NewCraftRecipeEntry {
    private final String itemResultName;
    private final String[] itemsRequireName;
    private NewItemEntry itemResult;
    private List<NewItemEntry> itemsRequires;
    private final int resultNumber;

    public NewCraftPatternShapeless(String name, String itemResultName, int resultNumber, String itemRequire, String... itemsRequireName) {
        super(name);
        this.itemResultName = itemResultName;
        this.resultNumber = resultNumber;
        this.itemsRequireName = new String[1 + itemsRequireName.length];
        this.itemsRequireName[0] = itemRequire;
        System.arraycopy(itemsRequireName, 0, this.itemsRequireName, 1, itemsRequireName.length);
    }

    public NewCraftPatternShapeless(String name, String itemResultName, String itemRequire, String... itemsRequireName) {
        this(name, itemResultName, 1, itemRequire, itemsRequireName);
    }

    @Override
    public Optional<NewCraftResult> craft(List<NewItemEntry> items) {
        return craftNonNull(items.stream().filter(Objects::nonNull).toList());
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        itemResult = RegistryUtils.evaluateSafe(rootRegistry, itemResultName, NewItemEntry.class);
        itemsRequires = new ArrayList<>(itemsRequireName.length);
        for (String itemRequireName : itemsRequireName) {
            itemsRequires.add(RegistryUtils.evaluateSafe(rootRegistry, itemRequireName, NewItemEntry.class));
        }
    }

    private Optional<NewCraftResult> craftNonNull(List<NewItemEntry> items) {
        if (items.size() != itemsRequires.size()) return Optional.empty();
        boolean[] indexDejaPris = new boolean[itemsRequires.size()];
        loop:
        for (NewItemEntry registerEntry : items) {
            for (int j = 0; j < itemsRequires.size(); j++) {
                if (!indexDejaPris[j] && registerEntry == itemsRequires.get(j)) {
                    indexDejaPris[j] = true;
                    continue loop;
                }
            }
        }
        for (boolean dejaPris : indexDejaPris) {
            if (!dejaPris) {
                return Optional.empty();
            }
        }
        return Optional.of(new NewCraftResult(itemResult, resultNumber));
    }
}
