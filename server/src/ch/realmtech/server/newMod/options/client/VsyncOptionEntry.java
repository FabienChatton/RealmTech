package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;
import com.badlogic.gdx.Gdx;

public class VsyncOptionEntry extends OptionClientEntry<Boolean> {
    public VsyncOptionEntry() {
        super("Vsync");
    }

    @Override
    protected Boolean getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueBoolean(optionLoader);
    }

    @Override
    public Boolean getDefaultValue() {
        return true;
    }

    @Override
    public void onValueChange(ClientContext clientContext, Boolean oldValue, Boolean newValue) {
        Gdx.graphics.setVSync(newValue);
    }
}
