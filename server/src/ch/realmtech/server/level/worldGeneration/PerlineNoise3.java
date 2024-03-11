package ch.realmtech.server.level.worldGeneration;


import ch.realmtech.server.newRegistry.NewCellEntry;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.RegistryUtils;

import java.util.Random;

public class PerlineNoise3 implements GetNoise {
    public final SimplexNoise simplexNoiseGround;
    public final SimplexNoise simplexNoiseCopper;
    public final SimplexNoise simplexNoiseStone;
    public final SimplexNoise simplexNoiseIron;
    public final SimplexNoise simplexNoiseCoal;
    public final SimplexNoise simplexNoiseGold;
    public final SimplexNoise simplexNoiseTree;

    public PerlineNoise3(long seed) {
        simplexNoiseGround = new SimplexNoise().setRand(new Random(seed));
        simplexNoiseCopper = new SimplexNoise().setRand(new Random(seed + 5431087549274L));
        simplexNoiseStone = new SimplexNoise().setRand(new Random(seed + 63914L));
        simplexNoiseIron = new SimplexNoise().setRand(new Random(seed + 436413941L));
        simplexNoiseCoal = new SimplexNoise().setRand(new Random(seed + 525974341L));
        simplexNoiseGold = new SimplexNoise().setRand(new Random(seed + 32179321L));
        simplexNoiseTree = new SimplexNoise().setRand(new Random(seed + 7312421L));
    }


    @Override
    public float getNoise(int x, int y, int octave, float roughness, float scale, SimplexNoise simplexNoise) {
        float ret = 0;
        float layerFrequency = scale;
        float layerWeight = 1;
        float weightSum = 0;
        for (int i = 0; i < octave; i++) {
            ret += (float) (simplexNoise.noise(x * layerFrequency, y * layerFrequency) * layerWeight);
            layerFrequency *= 2;
            weightSum += layerWeight;
            layerWeight *= roughness;
        }
        return ret;

    }

    public NewCellEntry getGroundDeco(PerlinNoise perlinNoise, int x, int y, NewRegistry<?> rootRegistry) {
        NewCellEntry ret = null;
        if (perlinNoise.getNoise(this, x, y, simplexNoiseGold) > 0.999999f) {
            ret = (NewCellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.goldOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseIron) > 0.999f) {
            ret = (NewCellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.ironOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseCopper) > 0.999f) {
            ret = (NewCellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.copperOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseCoal) > 0.999f) {
            ret = (NewCellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.coalOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseStone) > 0.999f) {
            ret = (NewCellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.stoneOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseTree) > 0.999f) {
            ret = (NewCellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.tree").get();
        }
        return ret;
    }
}
