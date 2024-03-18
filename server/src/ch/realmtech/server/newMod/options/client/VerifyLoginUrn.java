package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;

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
