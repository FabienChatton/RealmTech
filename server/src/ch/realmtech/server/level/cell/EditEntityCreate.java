package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;

public interface EditEntityCreate {
    void createEntity(ExecuteOnContext executeOnContext, int entityId);
}
