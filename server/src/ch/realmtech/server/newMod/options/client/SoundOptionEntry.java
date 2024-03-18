package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;

public class SoundOptionEntry extends OptionClientEntry<Integer> {
    public SoundOptionEntry() {
        super("Sound");
    }

    @Override
    protected Integer getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueInt(optionLoader);
    }

    @Override
    public Integer getDefaultValue() {
        return 100;
    }
}
