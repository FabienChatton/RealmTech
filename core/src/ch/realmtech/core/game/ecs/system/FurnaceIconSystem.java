package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.component.FurnaceExtraInfoComponent;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.FurnaceIconSystemForClient;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.registery.ItemRegisterEntry;
import ch.realmtech.server.registery.RegistryEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
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
    private ComponentMapper<LightComponent> mLight;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<InfChunkComponent> mChunk;

    @Override
    protected void process(int entityId) {
        FurnaceComponent furnaceComponent = mFurnace.get(entityId);
        FurnaceIconsComponent furnaceIconsComponent = mFurnaceIcons.get(entityId);
        FurnaceExtraInfoComponent furnaceExtraInfoComponent = mFurnaceExtraInfo.get(entityId);

        if (furnaceComponent.remainingTickToBurn > 0) {
            setIcon(furnaceIconsComponent.getIconFire(), "furnace-time-to-burn", furnaceComponent.remainingTickToBurn, furnaceExtraInfoComponent.lastRemainingTickToBurnFull);
            if (!mLight.has(entityId)) {
                CellComponent cellComponent = mCell.get(entityId);
                InfChunkComponent infChunkComponent = mChunk.get(cellComponent.chunkId);
                int worldPosX = MapManager.getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX());
                int worldPosY = MapManager.getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY());
                systemsAdminClient.getLightManager().createLight(entityId, Color.ORANGE, 6, worldPosX, worldPosY);
            }
        } else {
            if (mLight.has(entityId)) {
                systemsAdminClient.getLightManager().disposeLight(entityId);
            }
        }

        if (furnaceComponent.tickProcess >= 0) {
            setIcon(furnaceIconsComponent.getIconProcess(), "furnace-arrow", furnaceComponent.tickProcess, furnaceExtraInfoComponent.lastTickProcessFull);
        }
    }

    private void setIcon(int furnaceIconsComponent, String prefixTextureName, int ref, int max) {
        int iconProcessId = systemsAdminClient.inventoryManager.getTopItem(mInventory.get(furnaceIconsComponent).inventory[0]);
        ItemComponent iconProcessItemComponent = mItem.get(iconProcessId);
        String iconProcessTextureName = formatPourDix(ref, max, prefixTextureName);

        Optional<RegistryEntry<ItemRegisterEntry>> iconProcessTo = RealmTechCoreMod.ITEMS.getEnfants().stream()
                .filter((itemRegisterEntry) -> itemRegisterEntry.getEntry().getTextureRegionName().equals(iconProcessTextureName))
                .findFirst();
        if (iconProcessTo.isPresent()) {
            iconProcessItemComponent.itemRegisterEntry = iconProcessTo.get().getEntry();
        } else {
            logger.warn("Can not find icon, calculated texture: {}", iconProcessTextureName);
        }
    }

    private String formatPourDix(int ref, int max, String prefixTextureName) {
        int pourDix = (int) Math.min(10, Math.max(1, 1 + Math.floor(10f * ((float) ref / (float) max))));
        return String.format(prefixTextureName + (pourDix != 10 ? "-0%d" : "-%d"), pourDix);
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
        if (mLight.has(entityId)) {
            systemsAdminClient.getLightManager().disposeLight(entityId);
        }
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
            furnaceComponent.tickProcess = 0;
        }
    }
}
