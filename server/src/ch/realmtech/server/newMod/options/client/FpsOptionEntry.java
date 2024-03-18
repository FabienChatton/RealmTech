package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.mod.ClientContext;
import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;
import com.badlogic.gdx.Gdx;

public class FpsOptionEntry extends OptionClientEntry<Integer> {
    public FpsOptionEntry() {
        super("Fps");
    }

    @Override
    protected Integer getPropertyValue(OptionLoader optionLoader) {
        return getPropertyValueInt(optionLoader);
    }

    @Override
    public Integer getDefaultValue() {
        return 60;
    }

    @Override
    public void onValueChange(ClientContext clientContext, Integer oldValue, Integer newValue) {
        Gdx.graphics.setForegroundFPS(newValue);
    }
}
