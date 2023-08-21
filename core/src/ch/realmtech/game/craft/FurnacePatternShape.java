package ch.realmtech.game.craft;

import ch.realmtech.game.registery.ItemRegisterEntry;

import java.util.Optional;

public class FurnacePatternShape extends FurnacePattern {
    protected CraftPatternShape craftPatternShape;
    protected int timeToProcess;

    public FurnacePatternShape(ItemRegisterEntry itemResult, int timeToProcess, char[][] pattern, CraftPatternFix.CraftPatternArgs... args) {
        this(itemResult, timeToProcess, 1, pattern, args);
    }

    public FurnacePatternShape(ItemRegisterEntry itemResult, int timeToProcess, int nombre, char[][] pattern, CraftPatternFix.CraftPatternArgs... args) {
        craftPatternShape = new CraftPatternShape(itemResult, pattern, args);
        this.timeToProcess = timeToProcess;
    }

    @Override
    public Optional<CraftResult> craft(ItemRegisterEntry[] itemRegisterEntry, int width, int height) {
        Optional<CraftResult> craftResult = craftPatternShape.craft(itemRegisterEntry, width, height);
        if (craftResult.isPresent()) {
            CraftResult result = craftResult.get();
            return Optional.of(new CraftResult(result.getItemRegisterEntry(), result.getNombreResult(), timeToProcess));
        } else {
            return Optional.empty();
        }
    }
}
