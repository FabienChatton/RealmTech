package ch.realmtech.server.mod.options.server;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionServerEntry;

public class ServerNameOptionEntry extends OptionServerEntry<String> {
    public ServerNameOptionEntry() {
        super("ServerName");
    }

    @Override
    protected String getPropertyValue(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesServer(), getName(), getDefaultValue());
    }

    @Override
    public String getDefaultValue() {
        return "RealmTech-server";
    }
}
