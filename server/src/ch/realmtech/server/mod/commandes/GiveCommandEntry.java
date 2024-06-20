package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import ch.realmtech.server.registry.CommandEntry;
import ch.realmtech.server.registry.ItemEntry;
import ch.realmtech.server.registry.RegistryUtils;
import ch.realmtech.server.serialize.types.SerializedApplicationBytes;
import com.artemis.ComponentMapper;

import java.util.Optional;
import java.util.UUID;

import static picocli.CommandLine.*;

@Command(name = "give", description = "Give some item to a players")
public class GiveCommandEntry extends CommandEntry {
    public GiveCommandEntry() {
        super("Give");
    }

    @ParentCommand
    public MasterServerCommandNew masterServerCommand;

    @Parameters(index = "0", description = "The player identifier, username or uuid")
    private String playerIdentifier;

    @Parameters(index = "1", description = "The registry identifier of the item")
    private String itemRegistryId;

    @Parameters(index = "2", defaultValue = "1", description = "Number of items to give")
    private int numberOfItems;

    @Override
    public void run() {
        SystemsAdminServer systemsAdminServer = masterServerCommand.serverContext.getSystemsAdminServer();
        Optional<ItemEntry> itemRegisterEntry = RegistryUtils.findEntry(masterServerCommand.serverContext.getRootRegistry(), itemRegistryId);
        if (itemRegisterEntry.isEmpty()) {
            masterServerCommand.output.println(String.format("This item registry id doesn't existe, %s", itemRegistryId));
            return;
        }

        int playerId = systemsAdminServer.getPlayerManagerServer().getPlayerByUsernameOrUuid(playerIdentifier);
        if (playerId == -1) {
            masterServerCommand.output.println("Can not find player: " + playerIdentifier);
            return;
        }

        ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = masterServerCommand.getWorld().getMapper(PlayerConnexionComponent.class);

        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        InventoryComponent inventoryComponent = masterServerCommand.serverContext.getSystemsAdminServer().getInventoryManager().getChestInventory(playerId);
        int chestInventoryId = masterServerCommand.serverContext.getSystemsAdminServer().getInventoryManager().getChestInventoryId(playerId);
        for (int i = 0; i < numberOfItems; i++) {
            int itemId = systemsAdminServer.getItemManagerServer().newItemInventory(itemRegisterEntry.get(), UUID.randomUUID());
            if (!systemsAdminServer.getInventoryManager().addItemToInventory(inventoryComponent, itemId)) {
                // can not put item in inventory
                masterServerCommand.getWorld().delete(itemId);
                return;
            }
        }
        UUID chestInventoryUuid = systemsAdminServer.getUuidEntityManager().getEntityUuid(chestInventoryId);
        SerializedApplicationBytes ApplicationInventoryBytes = masterServerCommand.getSerializerController().getInventorySerializerManager().encode(inventoryComponent);
        masterServerCommand.serverContext.getServerConnexion().sendPacketTo(new InventorySetPacket(chestInventoryUuid, ApplicationInventoryBytes), playerConnexionComponent.channel);
    }
}
