package ch.realmtech.server.ecs;

import ch.realmtech.server.registry.Entry;

public interface Context {
    ExecuteOnContext getExecuteOnContext();

    Entry getDefaultSystemAdminClientEntry();
}
