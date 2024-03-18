package ch.realmtech.server.newRegistry;

import ch.realmtech.server.newMod.options.OptionLoader;

import java.util.concurrent.atomic.AtomicReference;

public abstract sealed class OptionEntry<T> extends NewEntry permits OptionClientEntry, OptionServerEntry {
    protected final AtomicReference<T> optionValue;
    protected OptionLoader optionLoader;

    public OptionEntry(String name) {
        super(name);
        optionValue = new AtomicReference<>(getDefaultValue());
    }

    @Override
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        optionLoader = RegistryUtils.evaluateSafe(rootRegistry, OptionLoader.class);
        setValueFromProperties();
    }

    public void setValueFromProperties() {
        setValue(getPropertyValue(optionLoader));
    }

    protected abstract T getPropertyValue(OptionLoader optionLoader);

    public abstract void setValue(T value);

    public abstract void setValue(String value);

    public void resetValue() {
        setValue(getDefaultValue());
    }

    public T getValue() {
        return optionValue.get();
    }

    public abstract T getDefaultValue();

    protected abstract Integer getPropertyValueInt(OptionLoader optionLoader);

    protected abstract String getPropertyValueString(OptionLoader optionLoader);

    protected abstract Boolean getPropertyValueBoolean(OptionLoader optionLoader);
}
