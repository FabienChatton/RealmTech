package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtechServer.ctrl.ItemManager;
import ch.realmtechServer.ctrl.ItemManagerCommun;
import ch.realmtechServer.ecs.component.Box2dComponent;
import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.component.PositionComponent;
import ch.realmtechServer.ecs.component.TextureComponent;
import ch.realmtechServer.ecs.system.InventoryManager;
import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class ItemManagerClient extends ItemManager {
    @Wire(name = "context")
    private RealmTech context;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private BodyDef bodyDef;
    @Wire
    private FixtureDef fixtureDef;
    private Archetype defaultItemGroundArchetype;
    private Archetype defaultItemInventoryArchetype;

    @Override
    protected void initialize() {
        super.initialize();
        defaultItemGroundArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .add(PositionComponent.class)
                .add(Box2dComponent.class)
                .add(TextureComponent.class)
                .build(world);
        defaultItemInventoryArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .add(TextureComponent.class)
                .build(world);
    }

    /**
     * Permet de faire apparaitre un nouvel item sur la map.
     * @param worldPosX La position X dans le monde du nouvel item.
     * @param worldPosY La position Y dans le monde du nouvel item.
     * @param itemRegisterEntry Le register qui permettra de créer l'item.
     */
    @Override
    public void newItemOnGround(float worldPosX, float worldPosY, ItemRegisterEntry itemRegisterEntry) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemGroundArchetype);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        TextureComponent textureComponent = world.edit(itemId).create(TextureComponent.class);
        textureComponent.set(itemRegisterEntry.getTextureRegion(context.getTextureAtlas()));
        textureComponent.scale = RealmTech.UNITE_SCALE;
        itemComponent.set(itemRegisterEntry);
        float widthWorld = textureComponent.texture.getRegionWidth() / RealmTech.PPM;
        float heightWorld = textureComponent.texture.getRegionHeight() / RealmTech.PPM;
        ItemManagerCommun.setItemPositionAndPhysicBody(world, physicWorld, bodyDef, fixtureDef, itemId, worldPosX, worldPosY, widthWorld, heightWorld);
    }

    @Override
    public int newItemInventory(ItemRegisterEntry itemRegisterEntry) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemInventoryArchetype);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        TextureComponent textureComponent = world.edit(itemId).create(TextureComponent.class);
        textureComponent.set(itemRegisterEntry.getTextureRegion(context.getTextureAtlas()));
        textureComponent.scale = RealmTech.UNITE_SCALE;
        itemComponent.set(itemRegisterEntry);
        return itemId;
    }

    public void playerPickUpItem(int itemId, int playerId) {
        world.edit(itemId).remove(Box2dComponent.class);
        world.edit(itemId).remove(PositionComponent.class);
        world.getSystem(InventoryManager.class).addItemToInventory(itemId, playerId);
    }
}
