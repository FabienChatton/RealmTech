package ch.realmtech.server.newMod.options.client;

import ch.realmtech.server.ecs.Context;
import ch.realmtech.server.newMod.options.OptionLoader;
import ch.realmtech.server.newRegistry.OptionClientEntry;
import com.badlogic.gdx.Gdx;

public class VsyncOptionEntry extends OptionClientEntry<Boolean> {
    private final Context context;

    public VsyncOptionEntry(Context context) {
        super("Vsync");
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
        return true;
    }

    @Override
    public void onValueChange(Boolean oldValue, Boolean newValue) {
        context.getExecuteOnContext().onClientContext(() -> Gdx.graphics.setVSync(newValue));
    }
}
