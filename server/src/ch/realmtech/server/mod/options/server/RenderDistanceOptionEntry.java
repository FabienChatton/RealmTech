package ch.realmtech.server.mod.options.server;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionServerEntry;

public class RenderDistanceOptionEntry extends OptionServerEntry<Integer> {
    public RenderDistanceOptionEntry() {
        super("RenderDistance");
    }

    @Override
    protected Integer getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueInt(optionLoader);
    }

    @Override
    public void setValue(String value) {
        optionLoader.setValueInt(this, value);
    }

    @Override
    public Integer getDefaultValue() {
        return 6;
    }
}
