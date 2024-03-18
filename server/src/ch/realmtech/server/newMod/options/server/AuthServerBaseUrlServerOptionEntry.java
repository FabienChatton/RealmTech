package ch.realmtech.server.newMod.options.server;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionServerEntry;

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
