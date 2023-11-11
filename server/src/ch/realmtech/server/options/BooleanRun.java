package ch.realmtech.server.options;

import java.util.function.Consumer;

public class BooleanRun {
    private boolean value;
    private final Consumer<Boolean> valueConsumer;

    public BooleanRun(final Consumer<Boolean> valueConsumer) {
        value = false;
        this.valueConsumer = valueConsumer;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    public Boolean get() {
        return value;
    }

    public void set(Boolean value) {
        this.value = value;
        valueConsumer.accept(value);
    }
}
