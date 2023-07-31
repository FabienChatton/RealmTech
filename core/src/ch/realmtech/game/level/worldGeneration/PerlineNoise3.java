package ch.realmtech.game.level.worldGeneration;


import java.util.Random;

public class PerlineNoise3 implements GetNoise {
    public final SimplexNoise simplexNoiseGround;
    public final SimplexNoise simplexNoiseCopper;
    public final SimplexNoise simplexNoiseTree;

    public PerlineNoise3(long seed) {
        simplexNoiseGround = new SimplexNoise();
        simplexNoiseGround.setRand(new Random(seed));
        simplexNoiseCopper = new SimplexNoise();
        simplexNoiseCopper.setRand(new Random(seed + 5431087549274L));
        simplexNoiseTree = new SimplexNoise();
        simplexNoiseTree.setRand(new Random(seed + 7312421L));
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
}
