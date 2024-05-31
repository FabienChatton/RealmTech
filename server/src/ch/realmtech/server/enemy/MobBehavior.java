package ch.realmtech.server.enemy;

import ch.realmtech.server.level.cell.EditEntity;

public class MobBehavior {
    private EditEntity editEntity;
    /**
     * default 1 heart dommage
     */
    private int attackDommage = 1;
    /**
     * default 15 tick attack cool down
     */
    private int attackCoolDownTick = 15;

    public static MobBehaviorBuilder builder(EditEntity editEntity) {
        return new MobBehaviorBuilder(editEntity);
    }

    public EditEntity getEditEntity() {
        return editEntity;
    }

    public int getAttackDommage() {
        return attackDommage;
    }

    public int getAttackCoolDownTick() {
        return attackCoolDownTick;
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

        public MobBehaviorBuilder attackDommage(int attackDommage) {
            mobBehavior.attackDommage = attackDommage;
            return this;
        }

        public MobBehaviorBuilder attackCoolDown(int attackCoolDownTick) {
            mobBehavior.attackCoolDownTick = attackCoolDownTick;
            return this;
        }
        public MobBehavior build() {
            return mobBehavior;
        }
    }
}
