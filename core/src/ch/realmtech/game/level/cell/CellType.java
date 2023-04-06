package ch.realmtech.game.level.cell;

import ch.realmtech.game.item.ItemType;

public enum CellType {
    GRASS("grass-01", new CellBehavior.Builder().breakWith(ItemType.PELLE).build()),
    SAND("sand-01", new CellBehavior.Builder().breakWith(ItemType.PELLE).build()),
    WATER("water-01", new CellBehavior.Builder().speedEffect(0.5f).build());

    public final String textureName;
    public final CellBehavior cellBehavior;

    CellType(String textureName, CellBehavior cellBehavior) {
        this.textureName = textureName;
        this.cellBehavior = cellBehavior;
    }
}
