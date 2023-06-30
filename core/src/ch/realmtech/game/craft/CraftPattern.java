package ch.realmtech.game.craft;

import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;

import java.util.Arrays;

public class CraftPattern implements CraftingRecipeEntry {
    final ItemRegisterEntry[] craftPattern;
    final ItemRegisterEntry itemResult;

    public CraftPattern(ItemRegisterEntry itemResult, char[] pattern, CraftPatternArgs... args) {
        if (args == null || args.length == 0) throw new IllegalArgumentException("Il manque l'argument du craft");
        this.itemResult = itemResult;
        int taillePattern = pattern.length;
        craftPattern = new ItemRegisterEntry[taillePattern];
        for (int i = 0; i < pattern.length; i++) {
            craftPattern[i] = trouveRegistreItemViaSymbole(args, pattern[i]);
        }
    }

    ItemRegisterEntry trouveRegistreItemViaSymbole(CraftPatternArgs[] args, char symbole) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].getSymbole() == symbole) {
                return args[i].getItem();
            }
        }
        return null;
    }

    @Override
    public ItemRegisterEntry craft(ItemRegisterEntry[] itemRegisterEntry) {
        return Arrays.equals(itemRegisterEntry, craftPattern) ? itemResult : null;
    }

    public static class CraftPatternArgs {
        char symbole;
        ItemRegisterEntry item;

        public CraftPatternArgs(char symbole, ItemRegisterEntry item) {
            this.symbole = symbole;
            this.item = item;
        }

        public char getSymbole() {
            return symbole;
        }

        public ItemRegisterEntry getItem() {
            return item;
        }
    }
}
