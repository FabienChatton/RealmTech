package ch.realmtech.server.newMod.options;

import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.OptionEntry;

public class VerifyTokenOptionEntry extends OptionEntry<Boolean> {
    public VerifyTokenOptionEntry() {
        super("VerifyAccessToken", false);
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {

    }
}
