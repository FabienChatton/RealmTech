package ch.realmtech.server.mod.options.server.mob;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionServerEntry;

public class EnemyFocusPlayerDstOptionEntry extends OptionServerEntry<Float> {
    public EnemyFocusPlayerDstOptionEntry() {
        super("EnemyFocusPlayerDstEntry");
    }

    @Override
    protected Float getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueFloat(optionLoader);
    }

    @Override
    public void setValue(String value) {
        optionValue.set(Float.parseFloat(value));
    }

    @Override
    public Float getDefaultValue() {
        return 5f;
    }
}
