package ch.realmtechServer.level.cell;

import ch.realmtechServer.ecs.component.InfCellComponent;
import ch.realmtechServer.ecs.component.InfChunkComponent;
import ch.realmtechServer.ecs.system.ItemManager;
import ch.realmtechServer.ecs.system.MapManager;
import ch.realmtechServer.item.ItemType;
import ch.realmtechServer.registery.ItemRegisterEntry;

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
                                MapManager.getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX()),
                                MapManager.getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY()),
                                itemRegisterEntry
                        );
                        world.getSystem(MapManager.class).damneCell(chunkId, cellId);
                        return true;
                    }
                }
            }
            return false;
        };
    }

    public BreakCell dropNothing() {
        return (world, chunkId, cellId, itemComponent, playerComponent) -> {
            world.getSystem(MapManager.class).damneCell(chunkId, cellId);
            return true;
        };
    }
}