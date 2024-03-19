package ch.realmtech.server.mod.options.server;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionServerEntry;

public class VerifyAccessTokenUrnOptionEntry extends OptionServerEntry<String> {
    public VerifyAccessTokenUrnOptionEntry() {
        super("VerifyAccessTokenUrn");
    }

    @Override
    protected String getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueString(optionLoader);
    }

    @Override
    public String getDefaultValue() {
        return "verifyAccessToken.php";
    }
}
