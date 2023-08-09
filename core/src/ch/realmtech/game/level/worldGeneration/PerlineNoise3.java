package ch.realmtech.game.level.worldGeneration;


import ch.realmtech.game.mod.RealmTechCoreMod;
import ch.realmtech.game.registery.CellRegisterEntry;

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
            ret += simplexNoise.noise(x * layerFrequency, y * layerFrequency) * layerWeight;
            layerFrequency *= 2;
            weightSum += layerWeight;
            layerWeight *= roughness;
        }
        return ret;

    }

    public CellRegisterEntry getGroundDeco(PerlinNoise perlinNoise, int x, int y) {
        CellRegisterEntry ret = null;
        if (perlinNoise.getNoise(this, x, y, simplexNoiseGold) > 0.999999f) {
            ret = RealmTechCoreMod.GOLD.cellRegisterEntry();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseIron) > 0.999f) {
            ret = RealmTechCoreMod.IRON.cellRegisterEntry();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseCopper) > 0.999f) {
            ret = RealmTechCoreMod.COPPER.cellRegisterEntry();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseCoal) > 0.999f) {
            ret = RealmTechCoreMod.COAL.cellRegisterEntry();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseStone) > 0.999f) {
            ret = RealmTechCoreMod.STONE.cellRegisterEntry();
        }
        if (perlinNoise.getNoise(this, x, y, simplexNoiseTree) > 0.999f) {
            ret = RealmTechCoreMod.TREE_CELL;
        }
        return ret;
    }
}
