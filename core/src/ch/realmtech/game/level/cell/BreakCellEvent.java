package ch.realmtech.game.level.cell;

import ch.realmtech.game.ecs.component.InfCellComponent;
import ch.realmtech.game.ecs.component.InfChunkComponent;
import ch.realmtech.game.ecs.system.ItemManager;
import ch.realmtech.game.ecs.system.MapSystem;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.registery.ItemRegisterEntry;

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
        return (world, chunkId, cellId, itemComponent, playerComponent) -> world.getSystem(MapSystem.class).damneCell(chunkId, cellId);
    }
}
