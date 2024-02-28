package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ctrl.ItemManager;
import ch.realmtech.server.ctrl.ItemManagerCommun;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.packet.clientPacket.ItemOnGroundPacket;
import ch.realmtech.server.packet.clientPacket.ItemOnGroundSupprimerPacket;
import ch.realmtech.server.registery.ItemRegisterEntry;
import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.UUID;

public class ItemManagerServer extends ItemManager {
    @Wire
    private SystemsAdminServer systemsAdminServer;
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
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;

    @Override
    protected void initialize() {
        super.initialize();
        defaultItemInventoryArchetype = new ArchetypeBuilder()
                .add(ItemComponent.class)
                .build(world);
        defaultItemGroundArchetype = new ArchetypeBuilder(defaultItemInventoryArchetype)
                .add(PositionComponent.class)
                .add(Box2dComponent.class)
                .build(world);
    }

    /**
     * Permet de faire apparaitre un nouvel item sur la map.
     *
     * @param worldPosX         La position X dans le monde du nouvel item.
     * @param worldPosY         La position Y dans le monde du nouvel item.
     * @param itemRegisterEntry Le register qui permettra de créer l'item.
     * @param itemUuid
     * @return
     */
    @Override
    public int newItemOnGround(float worldPosX, float worldPosY, ItemRegisterEntry itemRegisterEntry, UUID itemUuid) {
        final int itemId = ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemGroundArchetype, itemUuid);
        inventoryItemToGroundItem(itemId, worldPosX, worldPosY);
        return itemId;
    }

    @Override
    public int newItemInventory(ItemRegisterEntry itemRegisterEntry, UUID itemUuid) {
        return ItemManagerCommun.createNewItem(world, itemRegisterEntry, defaultItemInventoryArchetype, itemUuid);
    }

    public void inventoryItemToGroundItem(int itemId, float worldPosX, float worldPosY) {
        ItemManagerCommun.setItemPositionAndPhysicBody(world, physicWorld, bodyDef, fixtureDef, itemId, worldPosX, worldPosY, 0.9f, 0.9f);
        world.edit(itemId).create(ItemPickableComponent.class);
    }

    public void playerPickUpItem(int itemId, int playerId) {
        UUID uuid = systemsAdminServer.uuidEntityManager.getEntityUuid(itemId);
        PositionComponent playerPositionComponent = mPos.get(playerId);
        int chunkPosX = MapManager.getChunkPos(MapManager.getWorldPos(playerPositionComponent.x));
        int chunkPosY = MapManager.getChunkPos(MapManager.getWorldPos(playerPositionComponent.y));
        serverContext.getEcsEngineServer().nextTickSchedule(2, () -> serverContext.getServerHandler().sendPacketToSubscriberForChunkPos(new ItemOnGroundSupprimerPacket(uuid), chunkPosX, chunkPosY));
        ItemManagerCommun.removeBox2dAndPosition(itemId, mBox2d, physicWorld, world);
        world.edit(itemId).remove(ItemPickableComponent.class);
        systemsAdminServer.inventoryManager.addItemToInventory(systemsAdminServer.inventoryManager.getChestInventory(playerId), itemId);
    }

    public void dropItem(int itemId, float worldPosX, float worldPosY) {
        UUID itemUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(itemId);
        ItemComponent itemComponent = mItem.get(itemId);
        float dropWorldPosX = (float) (worldPosX + (Math.random() * 2f - 1f));
        float dropWorldPosY = (float) (worldPosX + (Math.random() * 2f - 1f));
        inventoryItemToGroundItem(itemId, dropWorldPosX, worldPosY);
        int chunkPosX = MapManager.getWorldPos(worldPosX);
        int chunkPosY = MapManager.getWorldPos(worldPosY);
        serverContext.getServerHandler().sendPacketToSubscriberForChunkPos(new ItemOnGroundPacket(itemUuid, itemComponent.itemRegisterEntry, dropWorldPosX, dropWorldPosY), chunkPosX, chunkPosY);
    }

}
