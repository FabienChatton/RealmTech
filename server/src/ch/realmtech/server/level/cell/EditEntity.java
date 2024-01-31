package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;

public interface EditEntity extends EditEntityCreate {
    void deleteEntity(ExecuteOnContext executeOnContext, int entityId);

    void replaceEntity(ExecuteOnContext executeOnContext, int entityId);

}

