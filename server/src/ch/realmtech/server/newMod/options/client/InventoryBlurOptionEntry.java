package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;

public class InventoryBlurOptionEntry extends OptionClientEntry<Boolean> {
    public InventoryBlurOptionEntry() {
        super("InventoryBlur");
    }

    @Override
    protected Boolean getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueBoolean(optionLoader);
    }

    @Override
    public Boolean getDefaultValue() {
        return true;
    }
}
