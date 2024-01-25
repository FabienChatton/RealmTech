package ch.realmtech.server.craft;


import ch.realmtech.server.registery.ItemRegisterEntry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The most permissive crafting pattern. If the item are in the
 * crafting table, do the craft. The items can be anywhere.
 * For a more restrictive craft pattern {@link CraftPatternShape}
 */
public class CraftPatternShapeless extends CraftPattern {
    private final ItemRegisterEntry itemResult;
    private final ItemRegisterEntry[] itemRequire;
    private final int nombre;

    public CraftPatternShapeless(ItemRegisterEntry itemResult, ItemRegisterEntry... itemRequire) {
        this(itemResult, 1, itemRequire);
    }

    public CraftPatternShapeless(ItemRegisterEntry itemResult, int nombre, ItemRegisterEntry... itemRequire) {
        if (itemRequire == null || itemRequire.length == 0)
            throw new IllegalArgumentException("Require items are require.");
        if (nombre <= 0) throw new IllegalArgumentException("Number of result item must be bigger than 0.");
        this.itemResult = itemResult;
        this.itemRequire = itemRequire;
        this.nombre = nombre;
    }

    @Override
    public Optional<CraftResult> craft(List<ItemRegisterEntry> itemRegisterEntry) {
        return craftNonNull(itemRegisterEntry.stream().filter(Objects::nonNull).toList());
    }

    private Optional<CraftResult> craftNonNull(List<ItemRegisterEntry> itemRegisterEntry) {
        if (itemRegisterEntry.size() != itemRequire.length) return Optional.empty();
        boolean[] indexDejaPris = new boolean[itemRequire.length];
        loop:
        for (ItemRegisterEntry registerEntry : itemRegisterEntry) {
            for (int j = 0; j < itemRequire.length; j++) {
                if (!indexDejaPris[j] && registerEntry == itemRequire[j]) {
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
        return Optional.of(new CraftResult(itemResult, nombre, 0));
    }
}
