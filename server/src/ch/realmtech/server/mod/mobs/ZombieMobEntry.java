package ch.realmtech.server.mod.mobs;

import ch.realmtech.server.enemy.MobBehavior;
import ch.realmtech.server.enemy.ZombieEditEntity;
import ch.realmtech.server.registry.MobEntry;

public class ZombieMobEntry extends MobEntry {
    public ZombieMobEntry() {
        super("Zombie");
    }

    @Override
    protected MobBehavior initializeMobBehavior() {
        return MobBehavior.builder(new ZombieEditEntity(this))
                .attackDommage(1)
                .attackCoolDown(15)
                .dropItemFqrn("realmtech.items.Sandales")
                .heart(10)
                .textures("zombie-0", "zombie-1", "zombie-2")
                .build();
    }
}
