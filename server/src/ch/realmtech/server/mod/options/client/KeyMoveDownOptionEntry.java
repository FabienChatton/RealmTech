package ch.realmtech.server.mod.options.client;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionClientEntry;
import com.badlogic.gdx.Input;

public class KeyMoveDownOptionEntry extends OptionClientEntry<Integer> {
    public KeyMoveDownOptionEntry() {
        super("KeyMoveDown");
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
        return Input.Keys.S;
    }
}
