package ch.realmtech.server.mod.options.client;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionClientEntry;

public class InventoryBlurOptionEntry extends OptionClientEntry<Boolean> {
    public InventoryBlurOptionEntry() {
        super("InventoryBlur");
    }

    @Override
    protected Boolean getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueBoolean(optionLoader);
    }

    @Override
    public void setValue(String value) {
        optionLoader.setValueBoolean(this, value);
    }

    @Override
    public Boolean getDefaultValue() {
        return true;
    }
}
