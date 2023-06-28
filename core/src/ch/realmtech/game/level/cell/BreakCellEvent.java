package ch.realmtech.game.level.cell;

import ch.realmtech.game.ecs.component.InfCellComponent;
import ch.realmtech.game.ecs.component.InfChunkComponent;
import ch.realmtech.game.ecs.system.ItemManager;
import ch.realmtech.game.ecs.system.MapSystem;
import ch.realmtech.game.registery.ItemRegisterEntry;

public class BreakCellEvent {

    public static BreakCell dropOnBreak(final ItemRegisterEntry itemRegisterEntry) {
        return (world, chunkId, cellId, itemComponent, playerComponent) -> {
            if (itemRegisterEntry != null) {
                InfCellComponent cellComponent = world.getMapper(InfCellComponent.class).get(cellId);
                InfChunkComponent infChunkComponent = world.getMapper(InfChunkComponent.class).get(chunkId);
                if (itemComponent != null && cellComponent != null && playerComponent != null) {
                    if (cellComponent.cellRegisterEntry.getCellBehavior().getBreakWith() == itemComponent.itemRegisterEntry.getItemBehavior().getItemType()) {
                        world.getSystem(ItemManager.class).newItemOnGround(
                                MapSystem.getWorldPos(infChunkComponent.chunkPosX, cellComponent.innerPosX),
                                MapSystem.getWorldPos(infChunkComponent.chunkPosY, cellComponent.innerPosY),
                                itemRegisterEntry
                        );
                        world.getSystem(MapSystem.class).damneCell(chunkId, cellId);
                    }
                }
            }
        };


    }

    public BreakCell dropNothing() {
        return (world, chunkId, cellId, itemComponent, playerComponent) -> {
            world.getSystem(MapSystem.class).damneCell(chunkId, cellId);
        };
    }
}
