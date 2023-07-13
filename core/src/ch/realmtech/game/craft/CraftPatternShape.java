package ch.realmtech.game.craft;

import ch.realmtech.game.mod.RealmTechCoreItem;
import ch.realmtech.game.registery.ItemRegisterEntry;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class CraftPatternShape extends CraftPattern {
    private ItemRegisterEntry[][] craftPattern2d;

    public CraftPatternShape(ItemRegisterEntry itemResult, char[] pattern, CraftPatternArgs... args) {
        super(itemResult, pattern, args);
    }

    public CraftPatternShape(ItemRegisterEntry itemResult, int nombre, char[] pattern, CraftPatternArgs... args) {
        super(itemResult, nombre, pattern, args);
        craftPattern2d = getCraftPattern2d();
    }

    private ItemRegisterEntry[][] getCraftPattern2d() {
        int largeur = Arrays.stream(craftPattern).toList().indexOf(null);
        int hauteur = (int) Arrays.stream(craftPattern).filter(Objects::isNull).count();
        if (largeur == -1 ) throw new IllegalArgumentException("Vous avez surement oublier un '\\'");
        return getItemRegisterEntries2d(hauteur, largeur, craftPattern);
    }

    private ItemRegisterEntry[][] getItemRegisterEntries2d(int hauteur, int largeur, ItemRegisterEntry[] itemRegisterEntries) {
        ItemRegisterEntry[][] ret = new ItemRegisterEntry[hauteur][largeur];
        for (int i = 0, l = 0, h = 0; i < itemRegisterEntries.length; i++) {
            if (i % hauteur == 0 && i != 0) {
                l++;
                h = 0;
            }
            if (itemRegisterEntries[i] == null) i++;
            if (i < hauteur * largeur) {
                ret[h++][l] = itemRegisterEntries[i];
            }
        }
        return ret;
    }


    @Override
    protected ItemRegisterEntry trouveRegistreItemViaSymbole(CraftPatternArgs[] args, char symbole) {
        if (symbole == '\n') return null;
        return super.trouveRegistreItemViaSymbole(args, symbole);
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
        for (int i = 0; i < itemRegisterEntries2d.length; i++) {
            for (int j = 0; j < craftPattern2d.length; j++) {
                for (int k = 0; k < craftPattern2d[j].length; k++) {
                    if (itemRegisterEntries2d[i][j] != craftPattern2d[j][k]) {
                        return Optional.empty();
                    }
                }
            }
        }
        return Optional.of(new CraftResult(itemResult, nombre));
    }
}
