package ch.realmtech.server.mod.options.client;

import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.registry.OptionClientEntry;

public class TiledTextureOptionEntry extends OptionClientEntry<Boolean> {
    public TiledTextureOptionEntry() {
        super("TiledTexture");
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
        return false;
    }
}
