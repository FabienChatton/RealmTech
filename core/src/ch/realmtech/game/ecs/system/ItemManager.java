package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.registery.ItemRegisterEntry;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

public class ItemManager extends Manager {
    @Wire(name ="context")
    RealmTech context;
    private Archetype defaultItemArchetype;

    @Override
    protected void initialize() {
        super.initialize();
        defaultItemArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .add(ToSaveComponent.class)
                .add(PositionComponent.class)
                .add(TextureComponent.class)
                .add(Box2dComponent.class)
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
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        itemComponent.set(itemRegisterEntry);
        TextureComponent textureComponent = world.edit(itemId).create(TextureComponent.class);
        setItemTexturePositionAndPhysicBody(itemId, textureComponent.texture = itemRegisterEntry.getTextureRegion(), worldPossX, worldPossY);
    }

    public void setItemTexturePositionAndPhysicBody(int itemId, TextureRegion texture, float worldPossX, float worldPossY) {
        PositionComponent positionComponent = world.edit(itemId).create(PositionComponent.class);
        positionComponent.set(worldPossX, worldPossY);
        TextureComponent textureComponent = world.edit(itemId).create(TextureComponent.class);
        textureComponent.set(texture);
        Box2dComponent box2dComponent = world.edit(itemId).create(Box2dComponent.class);
        Body itemBody = context.getEcsEngine().createBox2dItem(itemId, worldPossX, worldPossY, textureComponent.texture);
        box2dComponent.set(
                textureComponent.texture.getRegionWidth() / RealmTech.PPM,
                textureComponent.texture.getRegionHeight() / RealmTech.PPM,
                itemBody
        );
    }

    public void playerPickUpItem(int itemId, int playerId) {
        world.edit(itemId).remove(Box2dComponent.class);
        world.edit(itemId).remove(PositionComponent.class);
        world.getSystem(InventoryManager.class).addItemToPlayerInventory(itemId, playerId);
    }
}
