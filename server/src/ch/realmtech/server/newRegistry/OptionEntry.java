package ch.realmtech.server.newRegistry;

import java.util.concurrent.atomic.AtomicReference;

public abstract class OptionEntry<T> extends NewEntry {
    private final AtomicReference<T> optionValue;
    public OptionEntry(String name, T initialeValue) {
        super(name);
        optionValue = new AtomicReference<>(initialeValue);
    }

    public void setValue(T value) {
        optionValue.set(value);
    }

    public T getValue() {
        return optionValue.get();
    }
}
