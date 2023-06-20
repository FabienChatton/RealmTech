package ch.realmtech.game.level.worldGeneration;


import java.util.Random;

public class PerlineNoise3 implements GetNoise {
    public PerlineNoise3(Random seed) {
        SimplexNoise.setRand(seed);
    }

    @Override
    public float getNoise(int x, int y) {
        return (float) SimplexNoise.noise(((float) x) / 100f, ((float) y) / 100f);
    }
}
