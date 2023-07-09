package ch.realmtech.game.craft;

import ch.realmtech.game.registery.ItemRegisterEntry;

import java.util.Optional;

public class CraftPatternShapeless extends CraftPattern {
    public CraftPatternShapeless(ItemRegisterEntry itemResult, char[] pattern, CraftPatternArgs... args) {
        super(itemResult, pattern, args);
    }

    public CraftPatternShapeless(ItemRegisterEntry itemResult, int nombre, char[] pattern, CraftPatternArgs... args) {
        super(itemResult, nombre, pattern, args);
    }

    @Override
    public Optional<CraftResult> craft(ItemRegisterEntry[] itemRegisterEntry) {
        for (int i = 0; i < itemRegisterEntry.length; i++) {
            for (int j = 0; j < craftPattern.length; j++) {
                if (itemRegisterEntry[i] == craftPattern[j]) {
                    return Optional.of(new CraftResult(itemResult, nombre));
                }
            }
        }
        return Optional.empty();
    }
}
