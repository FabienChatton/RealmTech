package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;

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
