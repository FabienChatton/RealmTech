package ch.realmtech.server.newMod.options;

import ch.realmtech.server.newMod.EvaluateBefore;
import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.NewEntry;
import ch.realmtech.server.newRegistry.NewRegistry;

public class OptionLoader extends NewEntry {
    public OptionLoader() {
        super("Loader");
    }

    @Override
    @EvaluateBefore("#customOptions")
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {

    }


}
