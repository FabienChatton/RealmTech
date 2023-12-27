package ch.realmtech.server.serialize;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.ItemManagerServer;
import ch.realmtech.server.level.cell.ChestEditEntity;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.netty.ConnexionBuilder;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SerializerControllerTest {
    private static ServerContext serverContext;
    private static SerializerController serializerController;

    @BeforeAll
    static void startEmulator() throws Exception {
        serverContext = new ServerContext(new ConnexionBuilder().setSaveName("unitTest"));
        serializerController = serverContext.getSerializerController();
    }

    @AfterAll
    static void EndEmulator() throws Exception {
        serverContext.close();
    }

    @Test
    void serializeInventory() {
        InventoryManager inventoryManager = serverContext.getSystem(InventoryManager.class);
        ComponentMapper<ItemComponent> mItem = serverContext.getEcsEngineServer().getWorld().getMapper(ItemComponent.class);
        ComponentMapper<ChestComponent> mChest = serverContext.getEcsEngineServer().getWorld().getMapper(ChestComponent.class);
        ComponentMapper<InventoryComponent> mInventory = serverContext.getEcsEngineServer().getWorld().getMapper(InventoryComponent.class);
        int expectedChestId = serverContext.getEcsEngineServer().getWorld().create();
        int expectedInventoryId = serverContext.getEcsEngineServer().getSystemsAdminServer().inventoryManager.createChest(expectedChestId, UUID.randomUUID(), 3, 9);
        InventoryComponent expectedChestInventory = inventoryManager.getChestInventory(expectedChestId);
        for (int i = 0; i < 100; i++) {
            int newItemId = serverContext.getSystem(ItemManagerServer.class).newItemInventory(RealmTechCoreMod.PIOCHE_BOIS_ITEM, UUID.randomUUID());
            inventoryManager.addItemToInventory(expectedChestInventory, newItemId);
        }

        SerializedApplicationBytes serializedApplicationBytes = serializerController.getChestSerializerController().encode(expectedChestId);

        int chestMother = serverContext.getEcsEngineServer().getWorld().create();
        ChestEditEntity chestEditEntityArg = serializerController.getChestSerializerController().decode(serializedApplicationBytes);
        serverContext.getEcsEngineServer().getWorld().getSystem(InventoryManager.class).createChest(chestMother, chestEditEntityArg.getInventory(), chestEditEntityArg.getUuid(), chestEditEntityArg.getNumberOfSlotParRow(), chestEditEntityArg.getNumberOfRow());
        int chestInventoryId = serverContext.getSystem(InventoryManager.class).getChestInventoryId(chestMother);
        InventoryComponent actualChestInventory = mInventory.get(chestInventoryId);

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
        UuidComponent actualUuid = serverContext.getEcsEngineServer().getSystemsAdminServer().uuidComponentManager.getRegisteredComponent(chestInventoryId);

        assertEquals(expectedUuid, actualUuid);
    }

    @Test
    void serializeChunk() {
        ComponentMapper<InfChunkComponent> mChunk = serverContext.getEcsEngineServer().getWorld().getMapper(InfChunkComponent.class);
        InfMapComponent infMapComponent = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class);
        SaveMetadataComponent metaDonnesComponent = infMapComponent.getMetaDonnesComponent(serverContext.getEcsEngineServer().getWorld());
        int chunkId = serverContext.getEcsEngineServer().getSystemsAdminServer().mapManager.generateNewChunk(metaDonnesComponent, 0, 0);
        InfChunkComponent expectedInfChunkComponent = mChunk.get(chunkId);

        SerializedApplicationBytes expectedChunkEncoded = serializerController.getChunkSerializerController().encode(expectedInfChunkComponent);

        int actualChunkId = serializerController.getChunkSerializerController().decode(expectedChunkEncoded);
        InfChunkComponent actualInfChunkComponent = mChunk.get(actualChunkId);

        assertTrue(expectedInfChunkComponent.deepEquals(actualInfChunkComponent, serverContext.getEcsEngineServer().getWorld().getMapper(CellComponent.class)));
    }

    @Test
    void serializedSaveMetadata() {
        ComponentMapper<SaveMetadataComponent> mMetadata = serverContext.getEcsEngineServer().getWorld().getMapper(SaveMetadataComponent.class);
        InfMapComponent infMapComponent = serverContext.getEcsEngineServer().getMapEntity().getComponent(InfMapComponent.class);
        SaveMetadataComponent expectedMetaDonnesComponent = infMapComponent.getMetaDonnesComponent(serverContext.getEcsEngineServer().getWorld());

        SerializedApplicationBytes expectedSerializedSaveMetadata = serializerController.getSaveMetadataSerializerController().encode(infMapComponent.infMetaDonnees);

        int actualSaveMetadataId = serializerController.getSaveMetadataSerializerController().decode(expectedSerializedSaveMetadata);

        SaveMetadataComponent actualSaveMetadata = mMetadata.get(actualSaveMetadataId);
        assertEquals(expectedMetaDonnesComponent, actualSaveMetadata);

    }
}