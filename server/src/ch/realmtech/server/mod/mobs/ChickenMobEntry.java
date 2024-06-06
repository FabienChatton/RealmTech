package ch.realmtech.server.mod.mobs;

import ch.realmtech.server.enemy.EnemyTextureAnimated;
import ch.realmtech.server.enemy.MobBehavior;
import ch.realmtech.server.enemy.TextureRegionLazy;
import ch.realmtech.server.registry.MobEntry;

public class ChickenMobEntry extends MobEntry {
    public ChickenMobEntry() {
        super("Chicken");
    }

    @Override
    protected MobBehavior initializeMobBehavior() {
        return MobBehavior.builder(this)
                .textures(new EnemyTextureAnimated(1,
                        new TextureRegionLazy("chiken-front-0", "chiken-front-1", "chiken-front-2", "chiken-front-3"),
                        new TextureRegionLazy("chiken-back-0", "chiken-back-1", "chiken-back-2", "chiken-back-3"),
                        new TextureRegionLazy("chiken-left-0", "chiken-left-1", "chiken-left-2", "chiken-left-3"),
                        new TextureRegionLazy("chiken-right-0", "chiken-right-1", "chiken-right-2", "chiken-right-3")))
                .dropItemFqrn("realmtech.items.RawChicken")
                .build();
    }
}
