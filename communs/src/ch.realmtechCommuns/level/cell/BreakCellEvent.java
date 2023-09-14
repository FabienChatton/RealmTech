package ch.realmtechCommuns.level.cell;

import ch.realmtechCommuns.ecs.component.InfCellComponent;
import ch.realmtechCommuns.ecs.component.InfChunkComponent;
import ch.realmtechCommuns.ecs.system.ItemManager;
import ch.realmtechCommuns.ecs.system.MapSystem;
import ch.realmtechCommuns.item.ItemType;
import ch.realmtechCommuns.registery.ItemRegisterEntry;

public class BreakCellEvent {

    public static BreakCell dropOnBreak(final ItemRegisterEntry itemRegisterEntry) {
        return (world, chunkId, cellId, itemUse, playerComponent) -> {
            if (itemRegisterEntry != null) {
                InfCellComponent cellComponent = world.getMapper(InfCellComponent.class).get(cellId);
                InfChunkComponent infChunkComponent = world.getMapper(InfChunkComponent.class).get(chunkId);
                if (cellComponent != null && playerComponent != null) {
                    final ItemType itemTypeUse = itemUse != null
                            ? itemUse.itemRegisterEntry.getItemBehavior().getItemType()
                            : ItemType.TOUS;
                    if (cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() == ItemType.TOUS
                            || cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() == itemTypeUse) {
                        world.getSystem(ItemManager.class).newItemOnGround(
                                MapSystem.getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX()),
                                MapSystem.getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY()),
                                itemRegisterEntry
                        );
                        world.getSystem(MapSystem.class).damneCell(chunkId, cellId);
                        return true;
                    }
                }
            }
            return false;
        };
    }

    public BreakCell dropNothing() {
        return (world, chunkId, cellId, itemComponent, playerComponent) -> {
            world.getSystem(MapSystem.class).damneCell(chunkId, cellId);
            return true;
        };
    }
}
