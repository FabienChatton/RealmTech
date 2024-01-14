package ch.realmtech.server.craft;


import ch.realmtech.server.registery.ItemRegisterEntry;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CraftPatternShapeless extends CraftPattern {
    private final ItemRegisterEntry itemResult;
    private final ItemRegisterEntry[] itemRequire;
    private final int nombre;

    public CraftPatternShapeless(ItemRegisterEntry itemResult, ItemRegisterEntry... itemRequire) {
        this(itemResult, 1, itemRequire);
    }

    public CraftPatternShapeless(ItemRegisterEntry itemResult, int nombre, ItemRegisterEntry... itemRequire) {
        if (itemRequire == null || itemRequire.length == 0)
            throw new IllegalArgumentException("Il manque les items requit pour le craft");
        if (nombre <= 0) throw new IllegalArgumentException("Le nombre de résultat ne peut pas être nul ou négatif");
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
