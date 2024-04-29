package ch.realmtech.server.registry;

import ch.realmtech.server.mod.options.OptionLoader;

import java.util.concurrent.atomic.AtomicReference;

public abstract sealed class OptionEntry<T> extends Entry permits OptionClientEntry, OptionServerEntry {
    protected final AtomicReference<T> optionValue;
    protected OptionLoader optionLoader;

    public OptionEntry(String name) {
        super(name);
        optionValue = new AtomicReference<>(getDefaultValue());
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        optionLoader = RegistryUtils.evaluateSafe(rootRegistry, OptionLoader.class);
        setValueFromProperties();
    }

    public void setValueFromProperties() {
        setValue(getPropertyValue(optionLoader));
    }

    /**
     * Get the value from the property file. Please use the {@link #getPropertyValueInt} from int value
     * or {@link #getPropertyValueString} from string value.
     *
     * @param optionLoader The corresponding optionLoader matching the context.
     * @return The value from the property file.
     */
    protected abstract T getPropertyValue(OptionLoader optionLoader);

    public abstract void setValue(T value);

    public abstract void setValue(String value);

    public void resetValue() {
        setValue(getDefaultValue());
    }

    public T getValue() {
        return optionValue.get();
    }

    /**
     * Get the default value.
     * This value will be use if the option is reset.
     * @return The default value.
     */
    public abstract T getDefaultValue();

    protected abstract Integer getPropertyValueInt(OptionLoader optionLoader);

    protected abstract String getPropertyValueString(OptionLoader optionLoader);

    protected abstract Boolean getPropertyValueBoolean(OptionLoader optionLoader);

    protected abstract Float getPropertyValueFloat(OptionLoader optionLoader);
}
