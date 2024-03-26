package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ctrl.ItemManagerCommun;
import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.component.TextureComponent;
import ch.realmtech.server.registry.ItemEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Optional;
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
    private Archetype defaultItemGroundArchetype;
    private Archetype defaultItemInventoryArchetype;

    @Override
    protected void initialize() {
        super.initialize();
        defaultItemInventoryArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .add(TextureComponent.class)
                .build(world);
        defaultItemGroundArchetype = new ArchetypeBuilder(defaultItemInventoryArchetype)
                .add(PositionComponent.class)
                .add(Box2dComponent.class)
                .build(world);
    }

    @Override
    public int newItemOnGround(float worldPosX, float worldPosY, ItemEntry itemRegisterEntry, UUID itemUuid) {
        int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemGroundArchetype, itemUuid);
        ItemComponent itemComponent = mItem.get(itemId);
        TextureComponent textureComponent = mTexture.create(itemId);
        textureComponent.set(itemRegisterEntry.getTextureRegion(context.getTextureAtlas()));
        float widthWorld = textureComponent.texture.getRegionWidth() / RealmTech.PPM;
        float heightWorld = textureComponent.texture.getRegionHeight() / RealmTech.PPM;
        ItemManagerCommun.setItemPositionAndPhysicBody(world, physicWorld, bodyDef, fixtureDef, itemId, worldPosX, worldPosY, widthWorld, heightWorld);
        return itemId;
    }

    public void supprimeItemOnGround(UUID itemUuid) {
        int item = systemsAdminClient.getUuidEntityManager().getEntityId(itemUuid);
        if (item == -1) return;
        if (mItem.has(item)) {
            Box2dComponent box2dComponent = mBox2d.get(item);
            physicWorld.destroyBody(box2dComponent.body);
            world.delete(item);
        }
    }

    public void setItemOnGroundPos(UUID uuid, int itemRegisterEntryHash, float worldPosX, float worldPosY) {
        int itemId = systemsAdminClient.getUuidEntityManager().getEntityId(uuid);
        if (itemId == -1) {
            Optional<ItemEntry> itemEntry = RegistryUtils.findEntry(context.getRootRegistry(), itemRegisterEntryHash);
            if (itemEntry.isPresent()) {
                itemId = systemsAdminClient.getItemManagerClient().newItemOnGround(worldPosX, worldPosY, itemEntry.get(), uuid);
            }
        }
        if (!mBox2d.has(itemId)) {
            inventoryItemToGroundItem(itemId, worldPosX, worldPosY);
        }
        Box2dComponent box2dComponent = mBox2d.get(itemId);
        box2dComponent.body.setTransform(worldPosX, worldPosY, box2dComponent.body.getAngle());
    }

    public void inventoryItemToGroundItem(int itemId, float worldPosX, float worldPosY) {
        ItemManagerCommun.setItemPositionAndPhysicBody(world, physicWorld, bodyDef, fixtureDef, itemId, worldPosX, worldPosY, 0.9f, 0.9f);
    }

    @Override
    public int newItemInventory(ItemEntry itemRegisterEntry, UUID itemUuid) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemInventoryArchetype, itemUuid);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        itemComponent.set(itemRegisterEntry);
        return itemId;
    }
}
