package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ctrl.ItemManagerCommun;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.registery.ItemRegisterEntry;
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
    @Wire
    private SystemsAdminClient systemsAdminClient;
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
    private ComponentMapper<UuidComponent> mUuid;
    private Archetype defaultItemGroundArchetype;
    private Archetype defaultItemInventoryArchetype;

    @Override
    protected void initialize() {
        super.initialize();
        defaultItemInventoryArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .add(TextureComponent.class)
                .add(UuidComponent.class)
                .build(world);
        defaultItemGroundArchetype = new ArchetypeBuilder(defaultItemInventoryArchetype)
                .add(PositionComponent.class)
                .add(Box2dComponent.class)
                .build(world);
    }
    
    public int newItemOnGround(float worldPosX, float worldPosY, UUID itemUuid, ItemRegisterEntry itemRegisterEntry) {
        final int itemId = newItemOnGround(worldPosX, worldPosY, itemRegisterEntry, itemUuid);
        ItemComponent itemComponent = mItem.get(itemId);
        TextureComponent textureComponent = mTexture.get(itemId);
        itemComponent.set(itemRegisterEntry);
        float widthWorld = textureComponent.texture.getRegionWidth() / RealmTech.PPM;
        float heightWorld = textureComponent.texture.getRegionHeight() / RealmTech.PPM;
        ItemManagerCommun.setItemPositionAndPhysicBody(world, physicWorld, bodyDef, fixtureDef, itemId, worldPosX, worldPosY, widthWorld, heightWorld);
        return itemId;
    }

    @Override
    public int newItemOnGround(float worldPosX, float worldPosY, ItemRegisterEntry itemRegisterEntry, UUID itemUuid) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemGroundArchetype, itemUuid);
        EntityEdit edit = world.edit(itemId);
        TextureComponent textureComponent = edit.create(TextureComponent.class);
        textureComponent.set(itemRegisterEntry.getTextureRegion(context.getTextureAtlas()));
        textureComponent.scale = RealmTech.UNITE_SCALE;
        return itemId;
    }

    public void supprimeItemOnGround(UUID itemUuid) {
        int item = systemsAdminClient.uuidComponentManager.getRegisteredComponent(itemUuid, ItemComponent.class);
        if (item == -1) return;
        if (mItem.has(item)) {
            Box2dComponent box2dComponent = mBox2d.get(item);
            physicWorld.destroyBody(box2dComponent.body);
            world.delete(item);
        }
    }

    public void setItemOnGroundPos(UUID uuid, ItemRegisterEntry itemRegisterEntry, float worldPosX, float worldPosY) {
        IntBag items = world.getAspectSubscriptionManager().get(Aspect.all(ItemComponent.class, PositionComponent.class)).getEntities();
        int item = -1;
        for (int i = 0; i < items.size(); i++) {
            int itemId = items.get(i);
            if (mUuid.get(itemId).getUuid().equals(uuid)) {
                item = itemId;
                break;
            }
        }
        if (item == -1) {
            item = systemsAdminClient.getItemManagerClient().newItemOnGround(worldPosX, worldPosY, uuid, itemRegisterEntry);
        }
        Box2dComponent box2dComponent = mBox2d.get(item);
        box2dComponent.body.setTransform(worldPosX, worldPosY, box2dComponent.body.getAngle());
    }

    @Override
    public int newItemInventory(ItemRegisterEntry itemRegisterEntry, UUID itemUuid) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemInventoryArchetype, itemUuid);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        itemComponent.set(itemRegisterEntry);
        return itemId;
    }
}
