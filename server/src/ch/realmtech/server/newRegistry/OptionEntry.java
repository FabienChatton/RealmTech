package ch.realmtech.server.newRegistry;

import ch.realmtech.server.newMod.options.OptionLoader;

import java.util.concurrent.atomic.AtomicReference;

public abstract sealed class OptionEntry<T> extends NewEntry permits OptionClientEntry, OptionServerEntry {
    private final AtomicReference<T> optionValue;
    private OptionLoader optionLoader;

    public OptionEntry(String name) {
        super(name);
        optionValue = new AtomicReference<>(getDefaultValue());
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        optionLoader = RegistryUtils.evaluateSafe(rootRegistry, OptionLoader.class);
        setValue(getPropertyValue(optionLoader));
    }

    protected abstract T getPropertyValue(OptionLoader optionLoader);

    public void setValue(T value) {
        optionValue.set(value);
    }

    public T getValue() {
        return optionValue.get();
    }

    public abstract T getDefaultValue();

    protected Integer getPropertyValueInt(OptionLoader optionLoader) {
        return Integer.parseInt(optionLoader.getPropertiesServer().getProperty(getName(), Integer.toString((Integer) getDefaultValue())));
    }

    protected String getPropertyValueString(OptionLoader optionLoader) {
        return optionLoader.getPropertiesServer().getProperty(getName(), (String) getDefaultValue());
    }

    protected Boolean getPropertyValueBoolean(OptionLoader optionLoader) {
        return Boolean.parseBoolean(optionLoader.getPropertiesServer().getProperty(getName(), getDefaultValue().toString()));
    }
}
