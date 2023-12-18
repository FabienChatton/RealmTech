package ch.realmtech.server.level.cell;

import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.item.ItemType;
import ch.realmtech.server.registery.ItemRegisterEntry;

public class Cells {
    public enum Layer {
        GROUND((byte) 0),
        GROUND_DECO((byte) 1),
        BUILD((byte) 2),
        BUILD_DECO((byte) 3);
        public final byte layer;
        Layer(byte layer) {
            this.layer = layer;
        }
    }
    public static byte getInnerChunkPosY(byte innerChunkPos) {
        return (byte) (innerChunkPos & 0x0F);
    }

    public static byte getInnerChunkPosX(byte innerChunkPos) {
        return (byte) ((innerChunkPos >> 4) & 0x0F);
    }

    public static byte getInnerChunkPos(byte innerChunkPosX, byte innerChunkPosY) {
        return (byte) ((innerChunkPosX << 4) + innerChunkPosY);
    }

    public static boolean testRequireTools(ItemRegisterEntry itemUsed, CellComponent cellToBreak) {
        ItemType itemUsedType = itemUsed.getItemBehavior().getItemType();
        ItemType breakWithType = cellToBreak.cellRegisterEntry.getCellBehavior().getBreakWith();
        return itemUsedType == breakWithType;
    }

    public static boolean testRequireTools(ItemType itemUsedType, ItemType breakWithType) {
        return itemUsedType == breakWithType;
    }
}