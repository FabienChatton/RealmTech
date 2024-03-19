package ch.realmtech.server.level.worldGeneration;


import ch.realmtech.server.registry.CellEntry;
import ch.realmtech.server.registry.Registry;
import ch.realmtech.server.registry.RegistryUtils;

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

    public CellEntry getGroundDeco(PerlinNoise perlinNoise, int x, int y, Registry<?> rootRegistry) {
        CellEntry ret = null;
        if (perlinNoise.getNoise(this, x, y, simplexNoiseGold) > 0.999999f) {
            ret = (CellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.GoldOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseIron) > 0.999f) {
            ret = (CellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.IronOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseCopper) > 0.999f) {
            ret = (CellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.CopperOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseCoal) > 0.999f) {
            ret = (CellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.CoalOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseStone) > 0.999f) {
            ret = (CellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.StoneOre").get();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseTree) > 0.999f) {
            ret = (CellEntry) RegistryUtils.findEntry(rootRegistry, "realmtech.cells.Tree").get();
        }
        return ret;
    }
}
