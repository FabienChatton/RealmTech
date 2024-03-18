package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;
import com.badlogic.gdx.Gdx;

public class FullScreenOptionEntry extends OptionClientEntry<Boolean> {
    private final Context context;

    public FullScreenOptionEntry(Context context) {
        super("FullScreen");
        this.context = context;
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

    @Override
    public void onValueChange(Boolean oldValue, Boolean newValue) {
        context.getExecuteOnContext().onClientContext(() -> {
            if (newValue) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                Gdx.graphics.setWindowedMode(DataCtrl.SCREEN_WIDTH, DataCtrl.SCREEN_HEIGHT);
            }
        });
    }
}
