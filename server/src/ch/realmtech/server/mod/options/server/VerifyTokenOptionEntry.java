package ch.realmtech.server.mod.options.server;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionServerEntry;

public class VerifyTokenOptionEntry extends OptionServerEntry<Boolean> {
    public VerifyTokenOptionEntry() {
        super("VerifyAccessToken");
    }

    @Override
    protected Boolean getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueBoolean(optionLoader);
    }

    @Override
    public void setValue(String value) {
        optionLoader.setValueBoolean(this, value);
    }

    @Override
    public Boolean getDefaultValue() {
        return false;
    }
}
