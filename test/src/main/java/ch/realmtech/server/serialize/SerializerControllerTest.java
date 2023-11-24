package ch.realmtech.server.serialize;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.ChestComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.ItemManagerServer;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.netty.ConnexionBuilder;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class SerializerControllerTest {
    private final ServerContext serverContext;
    private final SerializerController serializerController;

    public SerializerControllerTest() throws Exception {
        serverContext = new ServerContext(new ConnexionBuilder().setSaveName("unitTest"));
        serializerController = serverContext.getSerializerController();
    }

    @Test
    void serializeInventory() {
        InventoryManager inventoryManager = serverContext.getSystem(InventoryManager.class);
        ComponentMapper<ItemComponent> mItem = serverContext.getEcsEngineServer().getWorld().getMapper(ItemComponent.class);
        ComponentMapper<ChestComponent> mChest = serverContext.getEcsEngineServer().getWorld().getMapper(ChestComponent.class);
        int expectedChestId = serverContext.getEcsEngineServer().getWorld().create();
        int expectedInventoryId = serverContext.getEcsEngineServer().getSystemsAdminServer().inventoryManager.createChest(expectedChestId, UUID.randomUUID(), 3, 9);
        InventoryComponent expectedChestInventory = inventoryManager.getChestInventory(expectedChestId);
        for (int i = 0; i < 100; i++) {
            int newItemId = serverContext.getSystem(ItemManagerServer.class).newItemInventory(RealmTechCoreMod.PIOCHE_BOIS_ITEM, UUID.randomUUID());
            inventoryManager.addItemToInventory(expectedChestInventory, newItemId);
        }

        SerializedApplicationBytes serializedApplicationBytes = serializerController.getChestSerializerController().encode(serverContext.getEcsEngineServer().getWorld(), expectedChestId);

        int chestDecode = serializerController.getChestSerializerController().decode(serverContext.getEcsEngineServer().getWorld(), serializedApplicationBytes);
        InventoryComponent actualChestInventory = inventoryManager.getChestInventory(chestDecode);

        assertEquals(expectedChestInventory.numberOfRow, actualChestInventory.numberOfRow);
        assertEquals(expectedChestInventory.numberOfSlotParRow, actualChestInventory.numberOfSlotParRow);

        for (int i = 0; i < expectedChestInventory.inventory.length; i++) {
            for (int j = 0; j < expectedChestInventory.inventory[i].length; j++) {
                int expectedItemId = expectedChestInventory.inventory[i][j];
                int actualItemId = actualChestInventory.inventory[i][j];
                if (expectedItemId != 0) {
                    ItemComponent expectedItemComponent = mItem.get(expectedItemId);
                    ItemComponent actualItemComponent = mItem.get(actualItemId);
                    assertSame(expectedItemComponent.itemRegisterEntry, actualItemComponent.itemRegisterEntry);

                    UuidComponent expectedUuidComponent = serverContext.getEcsEngineServer().getSystemsAdminServer().uuidComponentManager.getRegisteredComponent(expectedItemId);
                    UuidComponent actualUuidComponent = serverContext.getEcsEngineServer().getSystemsAdminServer().uuidComponentManager.getRegisteredComponent(actualItemId);
                    assertEquals(expectedUuidComponent, actualUuidComponent);
                }
            }
        }

        UuidComponent expectedUuid = serverContext.getEcsEngineServer().getSystemsAdminServer().uuidComponentManager.getRegisteredComponent(expectedInventoryId);
        UuidComponent actualUuid = serverContext.getEcsEngineServer().getSystemsAdminServer().uuidComponentManager.getRegisteredComponent(mChest.get(chestDecode).getInventoryId());

        assertEquals(expectedUuid, actualUuid);
    }
}