package ch.realmtech.server.mod.options.client;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionClientEntry;

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
