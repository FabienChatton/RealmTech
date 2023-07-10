package ch.realmtech.game.craft;


import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class CraftPatternShapeless implements CraftingRecipeEntry {
    private final ItemRegisterEntry itemResult;
    private final ItemRegisterEntry[] itemRequire;
    private final int nombre;
    public CraftPatternShapeless(ItemRegisterEntry itemResult, ItemRegisterEntry... itemRequire) {
        this(itemResult, 1, itemRequire);
    }

    public CraftPatternShapeless(ItemRegisterEntry itemResult, int nombre, ItemRegisterEntry... itemRequire) {
        if (itemRequire == null || itemRequire.length == 0) throw new IllegalArgumentException("Il manque les items requit pour le craft");
        if (nombre <= 0 ) throw new IllegalArgumentException("Le nombre de résultat ne peut pas être nul ou négatif");
        this.itemResult = itemResult;
        this.itemRequire = itemRequire;
        this.nombre = nombre;
    }

    @Override
    public Optional<CraftResult> craft(ItemRegisterEntry[] itemRegisterEntry) {
        return craftNonNull(Arrays.stream(itemRegisterEntry).filter(Objects::nonNull).toArray(ItemRegisterEntry[]::new));
    }

    private Optional<CraftResult> craftNonNull(ItemRegisterEntry[] itemRegisterEntry) {
        if (itemRegisterEntry.length != itemRequire.length) return Optional.empty();
        boolean[] indexDejaPris = new boolean[itemRequire.length];
        loop:
        for (int i = 0; i < itemRegisterEntry.length; i++) {
            for (int j = 0; j < itemRequire.length; j++) {
                if (!indexDejaPris[j] && itemRegisterEntry[i] == itemRequire[j]) {
                    indexDejaPris[j] = true;
                    continue loop;
                }
            }
        }
        for (int i = 0; i < indexDejaPris.length; i++) {
            if (!indexDejaPris[i]) {
                return Optional.empty();
            }
        }
        return Optional.of(new CraftResult(itemResult, nombre));
    }
}
