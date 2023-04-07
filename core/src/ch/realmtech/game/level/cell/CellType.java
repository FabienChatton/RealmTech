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

    public static byte getIdCellType(CellType cellType) {
        if (cellType == null) return 0;
        byte ret = 0;
        for (byte i = 0; i < CellType.values().length; i++) {
            CellType type = CellType.values()[i];
            if (cellType == type) {
                ret = (byte) (i + 1);
                break;
            }
        }
        return ret;
    }

    public static CellType getCellTypeByID(byte id) {
        if (id == 0) return null;
        CellType ret = null;
        for (int i = 1; i < CellType.values().length + 1; i++) {
            if (i == id) {
                ret = CellType.values()[id - 1];
                break;
            }
        }
        return ret;
    }
}
