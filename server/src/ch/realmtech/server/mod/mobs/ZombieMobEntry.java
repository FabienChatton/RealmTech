package ch.realmtech.server.mod.mobs;

import ch.realmtech.server.enemy.EnemySimpleTextureAnimated;
import ch.realmtech.server.enemy.MobBehavior;
import ch.realmtech.server.registry.MobEntry;

public class ZombieMobEntry extends MobEntry {
    public ZombieMobEntry() {
        super("Zombie");
    }

    @Override
    protected MobBehavior initializeMobBehavior() {
        return MobBehavior.builder(this)
                .attackDommage(1)
                .attackCoolDown(15)
                .dropItemFqrn("realmtech.items.Sandales")
                .heart(10)
                .focusPlayer()
                .textures(new EnemySimpleTextureAnimated(1.6f, "zombie-0", "zombie-1", "zombie-2"))
                .build();
    }
}
