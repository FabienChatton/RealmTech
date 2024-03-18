package ch.realmtech.server.newMod.options.server;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionServerEntry;

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
