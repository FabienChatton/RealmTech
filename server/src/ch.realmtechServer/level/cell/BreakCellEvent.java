package ch.realmtechServer.level.cell;

import ch.realmtechServer.ecs.component.InfCellComponent;
import ch.realmtechServer.ecs.component.InfChunkComponent;
import ch.realmtechServer.ecs.system.MapManager;
import ch.realmtechServer.item.ItemType;
import ch.realmtechServer.registery.ItemRegisterEntry;

public class BreakCellEvent {

    public static BreakCell dropOnBreak(final ItemRegisterEntry itemRegisterEntry) {
        return (itemManager, world, chunkId, cellId, itemUseByPlayer) -> {
            if (itemRegisterEntry != null) {
                InfCellComponent cellComponent = world.getMapper(InfCellComponent.class).get(cellId);
                InfChunkComponent infChunkComponent = world.getMapper(InfChunkComponent.class).get(chunkId);
                if (cellComponent != null) {
                    final ItemType itemTypeUse = itemUseByPlayer != null
                            ? itemUseByPlayer.getItemBehavior().getItemType()
                            : ItemType.TOUS;
                    if (cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() == ItemType.TOUS
                            || cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() == itemTypeUse) {
                        itemManager.newItemOnGround(
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

    public static BreakCell dropNothing() {
        return (itemManager, world, chunkId, cellId, itemUseByPlayer) -> {
            world.getSystem(MapManager.class).damneCell(chunkId, cellId);
            return true;
        };
    }
}
