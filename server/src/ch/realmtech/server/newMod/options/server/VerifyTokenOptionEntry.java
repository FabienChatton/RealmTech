package ch.realmtech.server.newMod.options.server;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionServerEntry;

public class VerifyTokenOptionEntry extends OptionServerEntry<Boolean> {
    public VerifyTokenOptionEntry() {
        super("VerifyAccessToken");
    }

    @Override
    protected Boolean getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueBoolean(optionLoader);
    }

    @Override
    public Boolean getDefaultValue() {
        return false;
    }
}
