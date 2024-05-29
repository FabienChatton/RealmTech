package ch.realmtech.server.enemy;

import ch.realmtech.server.level.cell.EditEntity;

public class MobBehavior {
    private EditEntity editEntity;

    public static MobBehaviorBuilder builder(EditEntity editEntity) {
        return new MobBehaviorBuilder(editEntity);
    }

    public EditEntity getEditEntity() {
        return editEntity;
    }

    public static class MobBehaviorBuilder {
        private final MobBehavior mobBehavior;

        private MobBehaviorBuilder(EditEntity editEntity) {
            mobBehavior = new MobBehavior();
            mobBehavior.editEntity = editEntity;
        }

        public MobBehaviorBuilder editEntity(EditEntity editEntity) {
            mobBehavior.editEntity = editEntity;
            return this;
        }

        public MobBehavior build() {
            return mobBehavior;
        }
    }
}
