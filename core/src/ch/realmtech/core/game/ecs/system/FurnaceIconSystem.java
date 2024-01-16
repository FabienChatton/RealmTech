package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.FurnaceExtraInfoComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.FurnaceComponent;
import ch.realmtech.server.ecs.component.FurnaceIconsComponent;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.plugin.FurnaceIconSystemForClient;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.registery.RegistryEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@All({FurnaceComponent.class, FurnaceIconsComponent.class, FurnaceExtraInfoComponent.class})
public class FurnaceIconSystem extends IteratingSystem implements FurnaceIconSystemForClient {
    private final static Logger logger = LoggerFactory.getLogger(FurnaceIconSystem.class);
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private SystemsAdminClient systemsAdminClient;

    private ComponentMapper<FurnaceIconsComponent> mFurnaceIcons;
    private ComponentMapper<FurnaceExtraInfoComponent> mFurnaceExtraInfo;
    private ComponentMapper<FurnaceComponent> mFurnace;
    private ComponentMapper<InventoryComponent> mInventory;
    private ComponentMapper<ItemComponent> mItem;
    @Override
    protected void process(int entityId) {
        FurnaceComponent furnaceComponent = mFurnace.get(entityId);
        FurnaceIconsComponent furnaceIconsComponent = mFurnaceIcons.get(entityId);
        FurnaceExtraInfoComponent furnaceExtraInfoComponent = mFurnaceExtraInfo.get(entityId);

        if (furnaceComponent.remainingTickToBurn > 0) {
            int iconFireItemId = systemsAdminClient.inventoryManager.getTopItem(mInventory.get(furnaceIconsComponent.getIconFire()).inventory[0]);
            ItemComponent iconFireItemComponent = mItem.get(iconFireItemId);
            String iconFireTextureName = formatPourDix(furnaceComponent.remainingTickToBurn, furnaceExtraInfoComponent.lastRemainingTickToBurnFull, "furnace-time-to-burn");

            Optional<RegistryEntry<ItemRegisterEntry>> iconFireTo = RealmTechCoreMod.ITEMS.getEnfants().stream()
                    .filter((itemRegisterEntry) -> itemRegisterEntry.getEntry().getTextureRegionName().equals(iconFireTextureName))
                    .findFirst();
            if (iconFireTo.isPresent()) {
                iconFireItemComponent.itemRegisterEntry = iconFireTo.get().getEntry();
            } else {
                logger.warn("Can not find icon fire, calculated texture: {}", iconFireTextureName);
            }
        }
    }

    private String formatPourDix(int ref, int max, String prefixTextureName) {
        int pourDix = (int) Math.min(10, Math.max(1, 1 + Math.floor(10f * ((float) ref / (float) max))));

        String format = String.format(prefixTextureName + (pourDix != 10 ? "-0%d" : "-%d"), pourDix);;
        return format;
    }

    @Override
    public void createIconFurnace(int motherEntity) {
        int iconFireId = world.create();
        int iconProcessId = world.create();

        systemsAdminClient.inventoryManager.createInventoryUiIcon(iconFireId, UUID.randomUUID(), new int[1][1], 1, 1);
        systemsAdminClient.inventoryManager.createInventoryUiIcon(iconProcessId, UUID.randomUUID(), new int[1][1], 1, 1);

        int iconFireItemId = systemsAdminClient.getItemManagerClient().newItemInventory(RealmTechCoreMod.ICON_FURNACE_TIME_TO_BURN_01, UUID.randomUUID());
        int iconProcessItemId = systemsAdminClient.getItemManagerClient().newItemInventory(RealmTechCoreMod.ICON_FURNACE_ARROW_01, UUID.randomUUID());

        systemsAdminClient.inventoryManager.addItemToInventory(iconFireId, iconFireItemId);
        systemsAdminClient.inventoryManager.addItemToInventory(iconProcessId, iconProcessItemId);
        world.edit(motherEntity).create(FurnaceIconsComponent.class).set(iconFireId, iconProcessId);
        world.edit(motherEntity).create(FurnaceExtraInfoComponent.class);
    }

    @Override
    public void deleteIconFurnace(int entityId) {
        FurnaceIconsComponent furnaceIconsComponent = mFurnaceIcons.get(entityId);
        systemsAdminClient.inventoryManager.removeInventory(mInventory.get(furnaceIconsComponent.getIconFire()).inventory);
        systemsAdminClient.inventoryManager.removeInventory(mInventory.get(furnaceIconsComponent.getIconProcess()).inventory);
    }

    public void setFurnaceExtraInfo(UUID furnaceUuid, int lastRemainingTickToBurnFull, int lastTickProcessFull) {
        int furnaceId = systemsAdminClient.uuidComponentManager.getRegisteredComponent(furnaceUuid, FurnaceComponent.class);
        FurnaceComponent furnaceComponent = mFurnace.get(furnaceId);
        FurnaceExtraInfoComponent furnaceExtraInfoComponent = mFurnaceExtraInfo.get(furnaceId);
        if (lastRemainingTickToBurnFull != -1) {
            furnaceExtraInfoComponent.lastRemainingTickToBurnFull = lastRemainingTickToBurnFull;
            furnaceComponent.remainingTickToBurn = lastRemainingTickToBurnFull;
        }
        if (lastTickProcessFull != -1) {
            furnaceExtraInfoComponent.lastTickProcessFull = lastTickProcessFull;
            furnaceComponent.tickProcess = lastTickProcessFull;
        }
    }
}
