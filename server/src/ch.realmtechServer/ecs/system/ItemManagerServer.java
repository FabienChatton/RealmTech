package ch.realmtechServer.ecs.system;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ctrl.ItemManager;
import ch.realmtechServer.ctrl.ItemManagerCommun;
import ch.realmtechServer.ecs.component.Box2dComponent;
import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.component.ItemPickableComponent;
import ch.realmtechServer.ecs.component.PositionComponent;
import ch.realmtechServer.packet.clientPacket.ItemOnGroundSupprimerPacket;
import ch.realmtechServer.registery.ItemRegisterEntry;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.UUID;

public class ItemManagerServer extends ItemManager {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private BodyDef bodyDef;
    @Wire
    private FixtureDef fixtureDef;
    private Archetype defaultItemGroundArchetype;
    private Archetype defaultItemInventoryArchetype;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<ItemComponent> mItem;

    @Override
    protected void initialize() {
        super.initialize();
        defaultItemGroundArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .add(PositionComponent.class)
                .add(Box2dComponent.class)
                .build(world);
        defaultItemInventoryArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .build(world);
    }

    /**
     * Permet de faire apparaitre un nouvel item sur la map.
     *
     * @param worldPosX         La position X dans le monde du nouvel item.
     * @param worldPosY         La position Y dans le monde du nouvel item.
     * @param itemRegisterEntry Le register qui permettra de créer l'item.
     * @return
     */
    @Override
    public int newItemOnGround(float worldPosX, float worldPosY, ItemRegisterEntry itemRegisterEntry) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemGroundArchetype);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        itemComponent.set(itemRegisterEntry, UUID.randomUUID());
        ItemManagerCommun.setItemPositionAndPhysicBody(world, physicWorld, bodyDef, fixtureDef, itemId, worldPosX, worldPosY, 0.9f, 0.9f);
        serverContext.getEcsEngineServer().nextTickSchedule(10, () -> {
            world.edit(itemId).create(ItemPickableComponent.class);
        });
        return itemId;
    }

    @Override
    public int newItemInventory(ItemRegisterEntry itemRegisterEntry) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemInventoryArchetype);
        ItemComponent itemComponent = world.edit(itemId).create(ItemComponent.class);
        itemComponent.set(itemRegisterEntry, UUID.randomUUID());
        return itemId;
    }

    public void playerPickUpItem(int itemId, int playerId) {
        ItemComponent itemComponent = mItem.get(itemId);
        serverContext.getServerHandler().broadCastPacket(new ItemOnGroundSupprimerPacket(itemComponent.uuid));
        ItemManagerCommun.removeBox2dAndPosition(itemId, mBox2d, physicWorld, world);
        world.edit(itemId).remove(ItemPickableComponent.class);
        world.getSystem(InventoryManager.class).addItemToInventory(itemId, playerId);
    }
}
