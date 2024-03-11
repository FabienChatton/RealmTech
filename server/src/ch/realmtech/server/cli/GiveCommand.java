package ch.realmtech.server.cli;


import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.ItemManagerServer;
import ch.realmtech.server.ecs.system.PlayerManagerServer;
import ch.realmtech.server.newRegistry.NewItemEntry;
import ch.realmtech.server.newRegistry.RegistryUtils;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "give", description = "Give some item to a players")
public class GiveCommand implements Callable<Integer> {
    @ParentCommand
    private MasterServerCommand masterCommand;

    @Parameters(index = "0", description = "The player identifier, username or uuid")
    private String playerIdentifier;

    @Parameters(index = "1", description = "The registry identifier of the item")
    private String itemRegistryId;

    @Parameters(index = "2", defaultValue = "1", description = "Number of items to give")
    private int numberOfItems;

    @Override
    public Integer call() throws Exception {
        SystemsAdminServer systemsAdminServer = masterCommand.serverContext.getSystemsAdmin();
        Optional<NewItemEntry> itemRegisterEntry = RegistryUtils.findEntry(masterCommand.serverContext.getRootRegistry(), itemRegistryId);
        if (itemRegisterEntry.isEmpty()) {
            masterCommand.output.println(String.format("This item registry id doesn't existe, %s", itemRegistryId));
            return 1;
        }

        int playerId = masterCommand.getWorld().getSystem(PlayerManagerServer.class).getPlayerByUsernameOrUuid(playerIdentifier);

        ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = masterCommand.getWorld().getMapper(PlayerConnexionComponent.class);

        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        InventoryComponent inventoryComponent = masterCommand.serverContext.getSystem(InventoryManager.class).getChestInventory(playerId);
        int chestInventoryId = masterCommand.serverContext.getSystem(InventoryManager.class).getChestInventoryId(playerId);
        for (int i = 0; i < numberOfItems; i++) {
            int itemId = masterCommand.getWorld().getSystem(ItemManagerServer.class).newItemInventory(itemRegisterEntry.get(), UUID.randomUUID());
            if (!masterCommand.getWorld().getSystem(InventoryManager.class).addItemToInventory(inventoryComponent, itemId)) {
                // can not put item in inventory
                masterCommand.getWorld().delete(itemId);
                return 0;
            }
        }
        UUID chestInventoryUuid = systemsAdminServer.uuidEntityManager.getEntityUuid(chestInventoryId);
        SerializedApplicationBytes ApplicationInventoryBytes = masterCommand.getSerializerManagerController().getInventorySerializerManager().encode(inventoryComponent);
        masterCommand.serverContext.getServerConnexion().sendPacketTo(new InventorySetPacket(chestInventoryUuid, ApplicationInventoryBytes), playerConnexionComponent.channel);
        return 0;
    }
}
