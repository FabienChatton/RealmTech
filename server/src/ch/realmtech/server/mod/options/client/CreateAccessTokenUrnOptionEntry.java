package ch.realmtech.server.mod.options.client;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionClientEntry;

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
