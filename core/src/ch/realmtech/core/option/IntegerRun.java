package ch.realmtech.core.option;

import java.util.function.Consumer;

public class IntegerRun {
    private int value;
    private final Consumer<Integer> valueChange;

    public IntegerRun(Consumer<Integer> valueChange) {
        value = 0;
        this.valueChange = valueChange;
    }
    public int get() {
        return value;
    }
    public void set(int value) {
        this.value = value;
        valueChange.accept(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
