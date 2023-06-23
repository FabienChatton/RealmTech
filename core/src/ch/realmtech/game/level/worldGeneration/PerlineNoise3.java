package ch.realmtech.game.level.worldGeneration;


import java.util.Random;

public class PerlineNoise3 implements GetNoise {
    public final SimplexNoise simplexNoiseGround;
    public final SimplexNoise simplexNoiseGroundDeco;
    public PerlineNoise3(long seed) {
        simplexNoiseGround = new SimplexNoise();
        simplexNoiseGround.setRand(new Random(seed));
        simplexNoiseGroundDeco = new SimplexNoise();
        simplexNoiseGroundDeco.setRand(new Random(seed + 5431087549274l));
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
