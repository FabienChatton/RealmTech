package ch.realmtech.server.mod.options.server;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionServerEntry;

public class AuthServerBaseUrlServerOptionEntry extends OptionServerEntry<String> {
    public AuthServerBaseUrlServerOptionEntry() {
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
