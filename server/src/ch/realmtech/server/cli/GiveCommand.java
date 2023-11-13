package ch.realmtech.server.cli;


import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.ecs.system.ItemManagerServer;
import ch.realmtech.server.ecs.system.PlayerManagerServer;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.packet.clientPacket.InventorySetPacket;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.registery.RegistryEntry;
import ch.realmtech.server.serialize.inventory.InventorySerializer;
import com.artemis.ComponentMapper;

import java.util.UUID;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "give", description = "Give some item to a players")
public class GiveCommand implements Callable<Integer> {
    @ParentCommand
    private MasterServerCommand masterCommand;

    @Parameters(index = "0", description = "The uuid of the player")
    private String playerUuid;

    @Parameters(index = "1", description = "The registry id of the item")
    private String itemRegistryId;

// pour une future implémentation
//    @Parameters(index = "2", defaultValue = "1", description = "Number of items to give")
//    private int numberOfItems;

    @Override
    public Integer call() throws Exception {
        RegistryEntry<ItemRegisterEntry> itemRegisterEntry = RealmTechCoreMod.ITEMS.get(itemRegistryId);
        if (itemRegisterEntry == null) {
            masterCommand.output.println(String.format("This item registry id doesn't existe, %s", itemRegistryId));
            return 1;
        }
        
        UUID uuid;
        try {
            uuid = UUID.fromString(playerUuid);
        } catch (IllegalArgumentException e) {
            masterCommand.output.println(e.getMessage());
            return -1;
        }
        ComponentMapper<InventoryComponent> mInventory = masterCommand.getWorld().getMapper(InventoryComponent.class);
        ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = masterCommand.getWorld().getMapper(PlayerConnexionComponent.class);
        ComponentMapper<UuidComponent> mUuid = masterCommand.getWorld().getMapper(UuidComponent.class);

        int playerId = masterCommand.getWorld().getSystem(PlayerManagerServer.class).getPlayerByUuid(uuid);

        PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(playerId);
        InventoryComponent inventoryComponent = mInventory.get(playerConnexionComponent.mainInventoryId);
        int itemId = masterCommand.getWorld().getSystem(ItemManagerServer.class).newItemInventory(itemRegisterEntry.getEntry());
        try {
            masterCommand.getWorld().getSystem(InventoryManager.class).addItemToInventory(inventoryComponent, itemId);
            byte[] inventoryBytes = InventorySerializer.toBytes(masterCommand.getWorld(), inventoryComponent);
            masterCommand.serverContext.getServerHandler().sendPacketTo(new InventorySetPacket(mUuid.get(playerConnexionComponent.mainInventoryId).getUuid(), inventoryBytes), playerConnexionComponent.channel);
        } catch (Exception e) {
            masterCommand.getWorld().delete(itemId);
            return -1;
        }
        return 0;
    }
}
