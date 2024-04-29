package ch.realmtech.server.registry;

import ch.realmtech.server.mod.options.OptionLoader;

public abstract non-sealed class OptionServerEntry<T> extends OptionEntry<T> {
    public OptionServerEntry(String name) {
        super(name);
    }

    @Override
    public void setValue(T value) {
        optionValue.set(value);
        optionLoader.getPropertiesServer().setProperty(getName(), value.toString());
    }

    @Override
    protected Integer getPropertyValueInt(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesServer(), getName(), (Integer) getDefaultValue());
    }

    @Override
    protected String getPropertyValueString(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesServer(), getName(), (String) getDefaultValue());
    }

    @Override
    protected Boolean getPropertyValueBoolean(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesServer(), getName(), (Boolean) getDefaultValue());
    }

    @Override
    protected Float getPropertyValueFloat(OptionLoader optionLoader) {
        return optionLoader.getPropertyOrCreate(optionLoader.getPropertiesServer(), getName(), (Float) getDefaultValue());
    }
}
