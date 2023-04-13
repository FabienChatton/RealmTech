package ch.realmtech.game.ecs.system;

import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.PositionComponent;
import ch.realmtech.game.ecs.component.TextureComponent;
import ch.realmtech.game.ecs.component.ToSaveComponent;
import ch.realmtech.game.registery.ItemRegisterEntry;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Manager;

public class ItemManager extends Manager {
    private Archetype defaultItemArchetype;

    @Override
    protected void initialize() {
        super.initialize();
        defaultItemArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .add(ToSaveComponent.class)
                .add(PositionComponent.class)
                .add(TextureComponent.class)
                .build(world);
    }

    /**
     * Permet de faire apparaitre un nouvel item sur la map.
     * @param worldPossX La position X dans le monde du nouvel item.
     * @param worldPossY La position Y dans le monde du nouvel item.
     * @param itemRegisterEntry Le register qui permettra de créer l'item.
     */
    public void newItem(float worldPossX, float worldPossY, ItemRegisterEntry itemRegisterEntry) {
        final int itemId;
        if (itemRegisterEntry.getArchetype() != null) {
            itemId = world.create(itemRegisterEntry.getArchetype());
        } else {
            itemId = world.create(defaultItemArchetype);
        }
        world.edit(itemId).create(ItemComponent.class);
        PositionComponent positionComponent = world.edit(itemId).create(PositionComponent.class);
        positionComponent.x = worldPossX;
        positionComponent.y = worldPossY;
        world.edit(itemId).create(TextureComponent.class).texture = itemRegisterEntry.getTextureRegion();
    }
}
