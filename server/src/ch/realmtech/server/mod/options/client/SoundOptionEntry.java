package ch.realmtech.server.mod.options.client;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.options.OptionSlider;
import ch.realmtech.server.registry.OptionClientEntry;

@OptionSlider(min = 0, max = 100, stepSize = 1)
public class SoundOptionEntry extends OptionClientEntry<Integer> {
    public SoundOptionEntry() {
        super("Sound");
    }

    @Override
    protected Integer getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueInt(optionLoader);
    }

    @Override
    public void setValue(String value) {
        Integer oldValue = getValue();
        optionLoader.setValueInt(this, value);
        if (getValue() > 100) {
            optionValue.set(oldValue);
            throw new IllegalArgumentException("Sound option can not be greater than 100");
        } else if (getValue() < 0) {
            optionValue.set(oldValue);
            throw new IllegalArgumentException("Sound option can not be less than 0");
        }
    }
    @Override
    public Integer getDefaultValue() {
        return 100;
    }
}
