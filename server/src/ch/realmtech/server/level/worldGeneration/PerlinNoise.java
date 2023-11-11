package ch.realmtech.server.level.worldGeneration;

import ch.realmtech.server.mod.RealmTechCoreMod;
import ch.realmtech.server.registery.CellRegisterEntry;
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

    public float getCopperNoise(int x, int y) {
        return perlinNoise.getNoise(x, y, OCTAVE, ROUGHNESS, SCALE, perlinNoise.simplexNoiseCopper);
    }

    public float getNoise(PerlineNoise3 perlinNoise, int x, int y, SimplexNoise simplexNoise) {
        return perlinNoise.getNoise(x, y, OCTAVE, ROUGHNESS, SCALE, simplexNoise);
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
        final CellRegisterEntry groundDecoCellRegisterEntry;
        if (groundCellRegisterEntry != RealmTechCoreMod.WATER_CELL) {
            groundDecoCellRegisterEntry = perlinNoise.getGroundDeco(this, x, y);
        } else {
            groundDecoCellRegisterEntry = null;
        }
        return new CellRegisterEntry[]{groundCellRegisterEntry, groundDecoCellRegisterEntry};
    }
}
