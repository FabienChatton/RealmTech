package ch.realmtech.server.newRegistry;

import ch.realmtech.server.mod.ClientContext;

public abstract non-sealed class OptionClientEntry<T> extends OptionEntry<T> {
    public OptionClientEntry(String name) {
        super(name);
    }

    public void onValueChange(ClientContext clientContext, T oldValue, T newValue) {
    }
}
