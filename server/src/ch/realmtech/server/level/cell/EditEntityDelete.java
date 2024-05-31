package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;

public interface EditEntityDelete {
    void deleteEntity(ExecuteOnContext executeOnContext, int entityId);
}
