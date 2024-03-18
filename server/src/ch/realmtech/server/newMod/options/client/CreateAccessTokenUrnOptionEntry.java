package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;

public class CreateAccessTokenUrnOptionEntry extends OptionClientEntry<String> {
    public CreateAccessTokenUrnOptionEntry() {
        super("CreateAccessTokenUrn");
    }

    @Override
    protected String getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueString(optionLoader);
    }

    @Override
    public String getDefaultValue() {
        return "createAccessToken.php";
    }
}
