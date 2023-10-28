package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtechServer.ctrl.ItemManager;
import ch.realmtechServer.ctrl.ItemManagerCommun;
import ch.realmtechServer.ecs.component.Box2dComponent;
import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.component.PositionComponent;
import ch.realmtechServer.ecs.component.TextureComponent;
import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.*;
import com.artemis.annotations.Wire;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.UUID;

public class ItemManagerClient extends ItemManager {
    @Wire(name = "context")
    private RealmTech context;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private BodyDef bodyDef;
    @Wire
    private FixtureDef fixtureDef;

    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<TextureComponent> mTexture;
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

    public int newItemOnGround(float worldPosX, float worldPosY, UUID itemUUID, ItemRegisterEntry itemRegisterEntry) {
        final int itemId = newItemOnGround(worldPosX, worldPosY, itemRegisterEntry);
        ItemComponent itemComponent = mItem.get(itemId);
        TextureComponent textureComponent = mTexture.get(itemId);
        itemComponent.set(itemRegisterEntry, itemUUID);
        float widthWorld = textureComponent.texture.getRegionWidth() / RealmTech.PPM;
        float heightWorld = textureComponent.texture.getRegionHeight() / RealmTech.PPM;
        ItemManagerCommun.setItemPositionAndPhysicBody(world, physicWorld, bodyDef, fixtureDef, itemId, worldPosX, worldPosY, widthWorld, heightWorld);
        return itemId;
    }

    @Override
    public int newItemOnGround(float worldPosX, float worldPosY, ItemRegisterEntry itemRegisterEntry) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemGroundArchetype);
        TextureComponent textureComponent = world.edit(itemId).create(TextureComponent.class);
        textureComponent.set(itemRegisterEntry.getTextureRegion(context.getTextureAtlas()));
        textureComponent.scale = RealmTech.UNITE_SCALE;
        return itemId;
    }


    public int newItemInventory(ItemRegisterEntry itemRegisterEntry, UUID itemUUID) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemInventoryArchetype);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        TextureComponent textureComponent = world.edit(itemId).create(TextureComponent.class);
        textureComponent.set(itemRegisterEntry.getTextureRegion(context.getTextureAtlas()));
        textureComponent.scale = RealmTech.UNITE_SCALE;
        itemComponent.set(itemRegisterEntry, itemUUID);
        return itemId;
    }

//    public void playerPickUpItem(int itemId, int playerId) {
//        world.edit(itemId).remove(Box2dComponent.class);
//        world.edit(itemId).remove(PositionComponent.class);
//        world.getSystem(InventoryManager.class).addItemToInventory(itemId, playerId);
//    }

    public void setItemOnGroundPos(UUID uuid, ItemRegisterEntry itemRegisterEntry, float worldPosX, float worldPosY) {
        IntBag entities = world.getAspectSubscriptionManager().get(Aspect.all(ItemComponent.class, PositionComponent.class)).getEntities();
        int item = -1;
        for (int i = 0; i < entities.size(); i++) {
            int itemId = entities.get(i);
            ItemComponent itemComponent = mItem.get(itemId);
            if (itemComponent.uuid.equals(uuid)) {
                item = itemId;
                break;
            }
        }
        if (item == -1) {
            item = newItemOnGround(worldPosX, worldPosY, uuid, itemRegisterEntry);
        }
        Box2dComponent box2dComponent = mBox2d.get(item);
        box2dComponent.body.setTransform(worldPosX, worldPosY, box2dComponent.body.getAngle());
    }

    @Override
    public int newItemInventory(ItemRegisterEntry itemRegisterEntry) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemInventoryArchetype);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        itemComponent.set(itemRegisterEntry, UUID.randomUUID());
        return itemId;
    }
}
