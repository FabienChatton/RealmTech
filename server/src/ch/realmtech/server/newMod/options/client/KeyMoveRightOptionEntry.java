package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;
import com.badlogic.gdx.Input;

public class KeyMoveRightOptionEntry extends OptionClientEntry<Integer> {
    public KeyMoveRightOptionEntry() {
        super("KeyMoveRight");
    }

    @Override
    protected Integer getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueInt(optionLoader);
    }

    @Override
    public Integer getDefaultValue() {
        return Input.Keys.D;
    }
}
