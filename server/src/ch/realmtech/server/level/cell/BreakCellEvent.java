package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.InfChunkComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.registry.ItemEntry;

public class BreakCellEvent {
    public static BreakCell dropOnBreak(ItemEntry dropItem) {
        return (cellManager, world, chunkId, cellId, itemUseByPlayer, playerSrc) -> {
            CellComponent cellToBreak = world.getMapper(CellComponent.class).get(cellId);
            InfChunkComponent infChunkComponent = world.getMapper(InfChunkComponent.class).get(chunkId);
            final ItemType itemUsedType = itemUseByPlayer != null ? itemUseByPlayer.getItemBehavior().getItemType() : ItemType.HAND;

            if (Cells.testRequireTools(itemUsedType, cellToBreak.cellRegisterEntry.getCellBehavior().getBreakWith())) {
                cellManager.breakCell(
                        MapManager.getWorldPos(infChunkComponent.chunkPosX, cellToBreak.getInnerPosX()),
                        MapManager.getWorldPos(infChunkComponent.chunkPosY, cellToBreak.getInnerPosY()),
                        dropItem,
                        playerSrc);
                return true;
            } else {
                return false;
            }
        };
    }

    public static BreakCell dropNothing() {
        return (cellManager, world, chunkId, cellId, itemUseByPlayer, playerSrc) -> {
            CellComponent cellComponent = world.getMapper(CellComponent.class).get(cellId);
            InfChunkComponent infChunkComponent = world.getMapper(InfChunkComponent.class).get(chunkId);
            int worldPosX = MapManager.getWorldPos(infChunkComponent.chunkPosX, cellComponent.getInnerPosX());
            int worldPosY = MapManager.getWorldPos(infChunkComponent.chunkPosY, cellComponent.getInnerPosY());
            cellManager.breakCell(worldPosX, worldPosY, null, playerSrc);
            return true;
        };
    }


}
