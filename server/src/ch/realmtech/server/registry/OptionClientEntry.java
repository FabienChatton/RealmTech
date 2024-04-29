package ch.realmtech.server.registry;

import ch.realmtech.server.mod.options.OptionLoader;

public abstract non-sealed class OptionClientEntry<T> extends OptionEntry<T> {
    public OptionClientEntry(String name) {
        super(name);
    }

    public void onValueChange(T oldValue, T newValue) {
    }

    @Override
    public void setValue(T value) {
        T oldValue = optionValue.get();
        optionValue.set(value);
        optionLoader.getPropertiesClient().setProperty(getName(), value.toString());
        onValueChange(oldValue, value);
    }

    @Override
    protected Integer getPropertyValueInt(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesClient(), getName(), (Integer) getDefaultValue());
    }

    @Override
    protected String getPropertyValueString(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesClient(), getName(), (String) getDefaultValue());
    }

    @Override
    protected Boolean getPropertyValueBoolean(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesClient(), getName(), (Boolean) getDefaultValue());
    }

    @Override
    protected Float getPropertyValueFloat(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesClient(), getName(), (Float) getDefaultValue());
    }
}
