package ch.realmtech.server.mod.options.server.mob;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionServerEntry;

public class MaxDstSpawnPlayerOptionEntry extends OptionServerEntry<Integer> {
    public MaxDstSpawnPlayerOptionEntry() {
        super("MaxDstSpawnPlayer");
    }

    @Override
    protected Integer getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueInt(optionLoader);
    }

    @Override
    public void setValue(String value) {
        optionValue.set(Integer.parseInt(value));
    }

    @Override
    public Integer getDefaultValue() {
        return null;
    }
}
