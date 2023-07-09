package ch.realmtech.game.craft;

import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;

import java.util.Arrays;
import java.util.Optional;

public final class CraftPattern implements CraftingRecipeEntry {
    private final ItemRegisterEntry[] craftPattern;
    private final ItemRegisterEntry itemResult;
    private int nombre;

    public CraftPattern(ItemRegisterEntry itemResult, char[] pattern, CraftPatternArgs... args) {
        if (args == null || args.length == 0) throw new IllegalArgumentException("Il manque l'argument du craft");
        this.itemResult = itemResult;
        int taillePattern = pattern.length;
        craftPattern = new ItemRegisterEntry[taillePattern];
        for (int i = 0; i < pattern.length; i++) {
            craftPattern[i] = trouveRegistreItemViaSymbole(args, pattern[i]);
        }
        nombre = 1;
    }

    public CraftPattern(ItemRegisterEntry itemResult, int nombre, char[] pattern, CraftPatternArgs... args) {
        this(itemResult, pattern, args);
        this.nombre = nombre;
    }

    private ItemRegisterEntry trouveRegistreItemViaSymbole(CraftPatternArgs[] args, char symbole) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].getSymbole() == symbole) {
                return args[i].getItem();
            }
        }
        return null;
    }

    @Override
    public Optional<CraftResult> craft(ItemRegisterEntry[] itemRegisterEntry) {
        if (Arrays.equals(itemRegisterEntry, craftPattern)) {
            return Optional.of(new CraftResult(itemResult, nombre));
        } else {
            return Optional.empty();
        }
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
