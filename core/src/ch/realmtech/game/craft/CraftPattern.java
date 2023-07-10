package ch.realmtech.game.craft;

import ch.realmtech.game.registery.CraftingRecipeEntry;
import ch.realmtech.game.registery.ItemRegisterEntry;

import java.util.Arrays;
import java.util.Optional;

public class CraftPattern implements CraftingRecipeEntry {
    protected final ItemRegisterEntry[] craftPattern;
    protected final ItemRegisterEntry itemResult;
    protected int nombre;

    public CraftPattern(ItemRegisterEntry itemResult, char[] pattern, CraftPatternArgs... args) {
        this(itemResult, 1, pattern, args);
    }

    public CraftPattern(ItemRegisterEntry itemResult, int nombre, char[] pattern, CraftPatternArgs... args) {
        if (args == null || args.length == 0) throw new IllegalArgumentException("Il manque l'argument du craft");
        if (nombre <= 0 ) throw new IllegalArgumentException("Le nombre de résultat ne peut pas être nul ou négatif");
        this.nombre = nombre;
        this.itemResult = itemResult;
        int taillePattern = pattern.length;
        craftPattern = new ItemRegisterEntry[taillePattern];
        for (int i = 0; i < pattern.length; i++) {
            craftPattern[i] = trouveRegistreItemViaSymbole(args, pattern[i]);
        }
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

        public CraftPatternArgs(char symbole, ItemRegisterEntry item) throws IllegalArgumentException {
            if (symbole == ' ') throw new IllegalArgumentException("le symbole ' ' ne peut pas être utilisé");
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
