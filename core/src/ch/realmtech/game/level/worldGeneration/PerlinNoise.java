package ch.realmtech.game.level.worldGeneration;

import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.registery.CellRegisterEntry;
import com.artemis.World;

import java.util.Random;

public class PerlinNoise {
    private final Random rand;
    private final PerlineNoise3 perlinNoise;
    private final static int OCTAVE = 7;
    private final static float ROUGHNESS = 0.6f;
    private final static float SCALE = 0.005f;
    private final World world;

    public PerlinNoise(Random rand, PerlineNoise3 perlinNoise, World world) {
        this.rand = rand;
        this.perlinNoise = perlinNoise;
        this.world = world;
    }


    public float getGroundNoise(int x, int y) {
       return perlinNoise.getNoise(x, y, OCTAVE, ROUGHNESS, SCALE, perlinNoise.simplexNoiseGround);
    }

    public float getGroundDecoNoise(int x, int y) {
        return perlinNoise.getNoise(x, y, 20, ROUGHNESS, SCALE, perlinNoise.simplexNoiseGroundDeco);
    }

    public CellRegisterEntry[] generateCell(int x, int y) {
        float ground = getGroundNoise(x, y);
        final CellRegisterEntry groundCellRegisterEntry;
        if (ground < 0.5f) {
            groundCellRegisterEntry = RealmTechCoreMod.GRASS_CELL;
        } else if (ground < 0.95f) {
            groundCellRegisterEntry = RealmTechCoreMod.SAND_CELL;
        } else {
            groundCellRegisterEntry = RealmTechCoreMod.WATER_CELL;
        }
        float groundDeco = getGroundDecoNoise(x, y);
        CellRegisterEntry groundDecoCellRegisterEntry = null;
        if (groundDeco > 0.999f) {
            if (groundCellRegisterEntry == RealmTechCoreMod.GRASS_CELL || groundCellRegisterEntry == RealmTechCoreMod.SAND_CELL) {
                groundDecoCellRegisterEntry = RealmTechCoreMod.TREE_CELL;
            }
        } else if (groundDeco > 0.998f && groundDeco < 0.999f) {
            groundDecoCellRegisterEntry = RealmTechCoreMod.COPPER_ORE;
        }
        return new CellRegisterEntry[]{groundCellRegisterEntry, groundDecoCellRegisterEntry};
    }
}
