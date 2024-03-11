package ch.realmtech.server.level.worldGeneration;

import ch.realmtech.server.newRegistry.NewCellEntry;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.RegistryUtils;
import com.artemis.World;

import java.util.Random;

public class PerlinNoise {
    private final Random rand;
    private final PerlineNoise3 perlinNoise;
    private final static int OCTAVE = 7;
    private final static float ROUGHNESS = 0.6f;
    private final static float SCALE = 0.005f;
    private final World world;
    private final NewRegistry rootRegistry;

    public PerlinNoise(Random rand, PerlineNoise3 perlinNoise, World world) {
        this.rand = rand;
        this.perlinNoise = perlinNoise;
        this.world = world;
        this.rootRegistry = world.getRegistered("rootRegistry");
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

    public NewCellEntry[] generateCell(int x, int y) {
        float ground = getGroundNoise(x, y);
        NewCellEntry groundCellRegisterEntry;
        if (ground < 0.5f) {
            groundCellRegisterEntry = (NewCellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.grass").get();
        } else if (ground < 0.95f) {
            groundCellRegisterEntry = (NewCellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.sand").get();
        } else {
            groundCellRegisterEntry = (NewCellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.water").get();
        }
        final NewCellEntry groundDecoCellRegisterEntry;
        if (!groundCellRegisterEntry.getName().equals("water")) {
            groundDecoCellRegisterEntry = perlinNoise.getGroundDeco(this, x, y, rootRegistry);
        } else {
            groundDecoCellRegisterEntry = null;
        }
        return new NewCellEntry[]{groundCellRegisterEntry, groundDecoCellRegisterEntry};
    }
}
