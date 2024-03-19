package ch.realmtech.server.mod.options.client;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionClientEntry;

public class VerifyLoginUrn extends OptionClientEntry<String> {
    public VerifyLoginUrn() {
        super("VerifyLoginUrn");
    }

    @Override
    protected String getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueString(optionLoader);
    }

    @Override
    public String getDefaultValue() {
        return "verifyPassword.php";
    }
}
