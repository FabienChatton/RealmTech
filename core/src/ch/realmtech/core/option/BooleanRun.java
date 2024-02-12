package ch.realmtech.core.option;

import ch.realmtech.core.RealmTech;

public class BooleanRun {
    private boolean value;
    private final OnBooleanRun valueConsumer;

    public BooleanRun(final OnBooleanRun valueConsumer) {
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

    public void set(Boolean value, RealmTech context) {
        this.value = value;
        valueConsumer.onBooleanRun(value, context);
    }
}

@FunctionalInterface
interface OnBooleanRun {
    void onBooleanRun(boolean value, RealmTech context);
}
