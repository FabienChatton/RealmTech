package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.component.InfCellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.registery.ItemRegisterEntry;

public class BreakCellEvent {

    public static BreakCell dropOnBreak(final ItemRegisterEntry itemRegisterEntry) {
        return (cellManager, world, chunkId, cellId, itemUseByPlayer) -> {
            if (itemRegisterEntry != null) {
                InfCellComponent cellComponent = world.getMapper(InfCellComponent.class).get(cellId);
                InfChunkComponent infChunkComponent = world.getMapper(InfChunkComponent.class).get(chunkId);
                if (cellComponent != null) {
                    final ItemType itemTypeUse = itemUseByPlayer != null
                            ? itemUseByPlayer.getItemBehavior().getItemType()
                            : ItemType.TOUS;
                    if (cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() == ItemType.TOUS
                            || cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() == itemTypeUse) {
                        cellManager.breakCell(
                                MapManager.getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX()),
                                MapManager.getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY()),
                                itemRegisterEntry
                        );
                        return true;
                    }
                }
            }
            return false;
        };
    }

    public static BreakCell dropNothing() {
        return (cellManager, world, chunkId, cellId, itemUseByPlayer) -> {
            world.getSystem(MapManager.class).damneCell(chunkId, cellId);
            return true;
        };
    }
}
