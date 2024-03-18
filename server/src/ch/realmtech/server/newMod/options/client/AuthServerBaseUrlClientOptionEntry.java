package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;

public class AuthServerBaseUrlClientOptionEntry extends OptionClientEntry<String> {
    public AuthServerBaseUrlClientOptionEntry() {
        super("AuthServerBaseUrl");
    }

    @Override
    protected String getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueString(optionLoader);
    }

    @Override
    public String getDefaultValue() {
        return "https://chattonf01.emf-informatique.ch/RealmTech/auth";
    }
}
