package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;

public interface EditEntity {
    void editEntity(ExecuteOnContext executeOnContext, int entityId);
}
