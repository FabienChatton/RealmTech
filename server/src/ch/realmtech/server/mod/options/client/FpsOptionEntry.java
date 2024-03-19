package ch.realmtech.server.mod.options.client;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.mod.options.OptionLoader;
import ch.realmtech.server.options.OptionSlider;
import ch.realmtech.server.registry.OptionClientEntry;
import com.badlogic.gdx.Gdx;

@OptionSlider(min = 0, max = 300, stepSize = 5)
public class FpsOptionEntry extends OptionClientEntry<Integer> {
    private final Context context;

    public FpsOptionEntry(Context context) {
        super("Fps");
        this.context = context;
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
        return 60;
    }

    @Override
    public void onValueChange(Integer oldValue, Integer newValue) {
        context.getExecuteOnContext().onClientContext(() -> Gdx.graphics.setForegroundFPS(newValue));
    }
}
