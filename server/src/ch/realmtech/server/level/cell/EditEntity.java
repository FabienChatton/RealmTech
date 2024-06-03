package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.ExecuteOnContext;

public interface EditEntity extends EditEntityDelete, EditEntityCreate {
    static EditEntity delete(EditEntityDelete editEntityDelete) {
        return new EditEntity() {
            @Override
            public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
            }

            @Override
            public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
                editEntityDelete.deleteEntity(executeOnContext, entityId);
            }
        };
    }

    static EditEntity create(EditEntityCreate editEntityCreate) {
        return new EditEntity() {
            @Override
            public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
                editEntityCreate.createEntity(executeOnContext, entityId);
            }

            @Override
            public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
            }
        };
    }

    /**
     * Merge two {@link EditEntity} to create one. The seconde will be executed after the first one.
     *
     * @param editEntity1 first to merge.
     * @param editEntity2 seconde to merge.
     * @return The new {@link EditEntity} created.
     */
    static EditEntity merge(EditEntity editEntity1, EditEntity editEntity2) {
        return new EditEntity() {
            @Override
            public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
                editEntity1.deleteEntity(executeOnContext, entityId);
                editEntity2.deleteEntity(executeOnContext, entityId);
            }

            @Override
            public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
                editEntity1.createEntity(executeOnContext, entityId);
                editEntity2.createEntity(executeOnContext, entityId);
            }
        };
    }

    static EditEntity merge(EditEntity... editEntities) {
        return new EditEntity() {
            @Override
            public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
                for (EditEntity entity : editEntities) {
                    entity.createEntity(executeOnContext, entityId);
                }
            }

            @Override
            public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
                for (EditEntity entity : editEntities) {
                    entity.deleteEntity(executeOnContext, entityId);
                }
            }

        };
    }

    static EditEntity empty() {
        return new EditEntity() {
            @Override
            public void createEntity(ExecuteOnContext executeOnContext, int entityId) {
            }

            @Override
            public void deleteEntity(ExecuteOnContext executeOnContext, int entityId) {
            }
        };
    }
}

