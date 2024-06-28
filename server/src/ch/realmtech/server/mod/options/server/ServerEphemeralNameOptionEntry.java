package ch.realmtech.server.mod.options.server;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.InvalideEvaluate;
import ch.realmtech.server.registry.OptionServerEntry;
import ch.realmtech.server.registry.Registry;

import java.util.UUID;

/**
 * This ephemeral name is used for identify the server.
 * The ephemeral name is reset for each server reboot.
 * This is used for player authentification see: <a href="https://github.com/FabienChatton/RealmTech-auth">Realmtech-auth</a>
 */
public class ServerEphemeralNameOptionEntry extends OptionServerEntry<String> {
    public ServerEphemeralNameOptionEntry() {
        super("ServerEphemeralName");
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        super.evaluate(rootRegistry);
        setValue(generateEphemeralName());
    }

    @Override
    protected String getPropertyValue(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesServer(), getName(), getDefaultValue());
    }

    @Override
    public String getDefaultValue() {
        return generateEphemeralName();
    }

    private String generateEphemeralName() {
        return UUID.randomUUID().toString();
    }
}
