package ch.realmtech.game.level.worldGeneration;


import java.util.Random;

public class PerlineNoise3 implements GetNoise {
    public PerlineNoise3(Random seed) {
        SimplexNoise.setRand(seed);
    }
    private final static int OCTAVE = 7;
    private final static float ROUGHNESS = 0.6f;
    private final static float SCALE = 0.005f;

    @Override
    public float getNoise(int x, int y) {
        float ret = 0;
        float layerFrequency = SCALE;
        float layerWeight = 1;
        float weightSum = 0;
        for (int i = 0; i < OCTAVE; i++) {
            ret += SimplexNoise.noise(x * layerFrequency, y * layerFrequency) * layerWeight;
            layerFrequency *= 2;
            weightSum += layerWeight;
            layerWeight *= ROUGHNESS;
        }
        return ret;

    }
}
