package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;
import com.badlogic.gdx.Gdx;

public class FullScreenOptionEntry extends OptionClientEntry<Boolean> {
    public FullScreenOptionEntry() {
        super("FullScreen");
    }

    @Override
    protected Boolean getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueBoolean(optionLoader);
    }

    @Override
    public Boolean getDefaultValue() {
        return false;
    }

    @Override
    public void onValueChange(ClientContext clientContext, Boolean oldValue, Boolean newValue) {
        if (newValue) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(DataCtrl.SCREEN_WIDTH, DataCtrl.SCREEN_HEIGHT);
        }
    }
}
