package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.craft.CraftResultChange;
import ch.realmtech.server.ecs.component.CraftingTableComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

import java.util.Optional;

@All(CraftingTableComponent.class)
public class CraftingSystem extends IteratingSystem {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;
    private ComponentMapper<CraftingTableComponent> mCraftingTable;
    private ComponentMapper<UuidComponent> mUuid;

    @Override
    protected void process(int entityId) {
        CraftingTableComponent craftingTableComponent = mCraftingTable.get(entityId);

        Optional<CraftResultChange> craftResultChangeOpt = craftingTableComponent.getIsCraftResultChange().apply(entityId);
        craftResultChangeOpt.ifPresent((craftResultChange) -> {
            craftingTableComponent.getOnCraftResultChange(world).accept(craftResultChange.craftResult());
        });
    }

}
