package ch.realmtech.server.newRegistry;

import java.util.List;

public interface NewRegistryUnmodified {
    List<NewRegistry> getChildRegistries();

    List<NewEntry> getEntries();
}
