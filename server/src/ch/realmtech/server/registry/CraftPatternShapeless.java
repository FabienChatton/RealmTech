package ch.realmtech.server.registry;

import ch.realmtech.server.craft.CraftResult;

import java.util.*;

/**
 * Shapeless pattern are valide craft when require item are present in the inventory. <br />
 * For exemple: 1 wood log => 4 planks
 * <pre>
 * +----------------------------+
 * | +-----+-----+              |
 * | | log |     |              |
 * | +-----+-----+   valide     |
 * | |     |     |              |
 * | +-----+-----+              |
 * |                            |
 * | +-----+-----+              |
 * | |     |     |              |
 * | +-----+-----+   valide     |
 * | |     | log |              |
 * | +-----+-----+              |
 * |                            |
 * | +-----+-----+              |
 * | |     | log |              |
 * | +-----+-----+   invalide   |
 * | |     | log |   only 1 log |
 * | +-----+-----+   is defined |
 * +----------------------------+
 * </pre>
 */
public abstract class CraftPatternShapeless extends CraftRecipeEntry {
    private final String itemResultName;
    private final String[] itemsRequireName;
    private ItemEntry itemResult;
    private List<ItemEntry> itemsRequires;
    private final int resultNumber;

    public CraftPatternShapeless(String name, String itemResultName, int resultNumber, String itemRequire, String... itemsRequireName) {
        super(name);
        this.itemResultName = itemResultName;
        this.resultNumber = resultNumber;
        this.itemsRequireName = new String[1 + itemsRequireName.length];
        this.itemsRequireName[0] = itemRequire;
        System.arraycopy(itemsRequireName, 0, this.itemsRequireName, 1, itemsRequireName.length);
    }

    public CraftPatternShapeless(String name, String itemResultName, String itemRequire, String... itemsRequireName) {
        this(name, itemResultName, 1, itemRequire, itemsRequireName);
    }

    @Override
    public Optional<CraftResult> craft(List<List<ItemEntry>> items) {
        if (items.stream().flatMap(Collection::stream).filter(Objects::nonNull).toList().equals(itemsRequires)) {
            return Optional.of(new CraftResult(itemResult, resultNumber));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<List<ItemEntry>> getRequireItems() {
        return List.of(itemsRequires);
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        itemResult = RegistryUtils.evaluateSafe(rootRegistry, itemResultName, ItemEntry.class);
        itemsRequires = new ArrayList<>(itemsRequireName.length);
        for (String itemRequireName : itemsRequireName) {
            itemsRequires.add(RegistryUtils.evaluateSafe(rootRegistry, itemRequireName, ItemEntry.class));
        }
    }

    private Optional<CraftResult> craftNonNull(List<ItemEntry> items) {
        if (items.size() != itemsRequires.size()) return Optional.empty();
        boolean[] indexDejaPris = new boolean[itemsRequires.size()];
        loop:
        for (ItemEntry registerEntry : items) {
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
        return Optional.of(new CraftResult(itemResult, resultNumber));
    }
}
