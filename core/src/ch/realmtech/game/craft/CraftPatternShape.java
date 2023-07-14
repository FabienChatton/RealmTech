package ch.realmtech.game.craft;

import ch.realmtech.game.mod.RealmTechCoreItem;
import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static ch.realmtech.game.craft.CraftPattern.CraftPatternArgs;
import static ch.realmtech.game.craft.CraftPattern.getCraftPatternFromArgs;

public class CraftPatternShape implements CraftingRecipeEntry {
    protected final ItemRegisterEntry itemResult;
    protected final int nombre;
    private final ItemRegisterEntry[][] craftPattern2d;

    public CraftPatternShape(ItemRegisterEntry itemResult, char[][] pattern, CraftPatternArgs... args) {
        this(itemResult, 1, pattern, args);
    }

    public CraftPatternShape(ItemRegisterEntry itemResult, int nombre, char[][] pattern, CraftPatternArgs... args) {
        this.itemResult = itemResult;
        this.nombre = nombre;
        craftPattern2d = getCraftPattern2d(pattern, args);
    }

    private ItemRegisterEntry[][] getCraftPattern2d(char[][] pattern, CraftPatternArgs[] args) {
        ItemRegisterEntry[][] ret = new ItemRegisterEntry[pattern.length][];
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[i].length; j++) {
                ret[i] = getCraftPatternFromArgs(pattern[i], args);
            }
        }
        return ret;
    }

    private ItemRegisterEntry[][] getItemRegisterEntries2d(int hauteur, int largeur, ItemRegisterEntry[] itemRegisterEntries) {
        ItemRegisterEntry[][] ret = new ItemRegisterEntry[largeur][hauteur];
        for (int i = 0, l = 0, h = 0; i < itemRegisterEntries.length; i++) {
            if (i % hauteur == 0 && i != 0) {
                l++;
                h = 0;
            }
            if (itemRegisterEntries[i] == null) i++;
            if (i < hauteur * largeur || i < itemRegisterEntries.length) {
                ret[l][h++] = itemRegisterEntries[i];
            }
        }
        return ret;
    }

    @Override
    public Optional<CraftResult> craft(ItemRegisterEntry[] itemRegisterEntry) {
        int taille2d = (int) Math.sqrt(itemRegisterEntry.length);
        ItemRegisterEntry[] pureItemRegisterEntry = new ItemRegisterEntry[itemRegisterEntry.length];
        for (int i = 0; i < itemRegisterEntry.length; i++) {
            if (itemRegisterEntry[i] == null) {
                pureItemRegisterEntry[i] = RealmTechCoreItem.NO_ITEM;
            } else {
                pureItemRegisterEntry[i] = itemRegisterEntry[i];
            }
        }
        ItemRegisterEntry[][] itemRegisterEntries2d = getItemRegisterEntries2d(taille2d, taille2d, pureItemRegisterEntry);
        try {
            loop:
            for (int ih = 0; ih < itemRegisterEntries2d.length; ih++) {
                for (int il = 0; il < itemRegisterEntries2d[ih].length; il++) {
                    for (int l = 0; l < craftPattern2d.length; l++) {
                        for (int h = 0; h < craftPattern2d[l].length; h++) {
                            if (itemRegisterEntries2d[il + l][ih + h] != craftPattern2d[l][h]) {
                                continue loop;
                            }
                        }
                    }
                }
                return Optional.of(new CraftResult(itemResult, nombre));
            }
        } catch (IndexOutOfBoundsException e) {

        }
        return Optional.empty();
    }

    @Override
    public List<ItemRegisterEntry> getRequireItem() {
        return Arrays.stream(craftPattern2d).flatMap(Arrays::stream).toList();
    }
}
