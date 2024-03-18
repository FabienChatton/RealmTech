package ch.realmtech.server.newMod.options.server;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionServerEntry;

public class RenderDistanceOptionEntry extends OptionServerEntry<Integer> {
    public RenderDistanceOptionEntry() {
        super("RenderDistance");
    }

    @Override
    protected Integer getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueInt(optionLoader);
    }

    @Override
    public Integer getDefaultValue() {
        return 6;
    }
}
