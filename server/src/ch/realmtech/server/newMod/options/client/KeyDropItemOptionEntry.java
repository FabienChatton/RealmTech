package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;
import com.badlogic.gdx.Input;

public class KeyDropItemOptionEntry extends OptionClientEntry<Integer> {
    public KeyDropItemOptionEntry() {
        super("KeyDropItem");
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
        return Input.Keys.Q;
    }
}
