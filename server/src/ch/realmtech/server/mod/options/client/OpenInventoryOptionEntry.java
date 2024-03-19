package ch.realmtech.server.mod.options.client;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionClientEntry;
import com.badlogic.gdx.Input;

public class OpenInventoryOptionEntry extends OptionClientEntry<Integer> {
    public OpenInventoryOptionEntry() {
        super("OpenInventory");
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
        return Input.Keys.E;
    }
}
