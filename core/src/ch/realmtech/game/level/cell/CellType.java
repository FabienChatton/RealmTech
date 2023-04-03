package ch.realmtech.game.level.cell;

public enum CellType {
    GRASS("grass-01"),
    SAND("sand-01"),
    WATER("water-01");

    public final String textureName;

    CellType(String textureName) {
        this.textureName = textureName;
    }
}
