package ch.realmtech.game.level.worldGeneration;

import java.util.Random;

public class PerlineNoise2 implements GeneratePerlinNoise {
    private final int octaves;
    private final float roughness;
    private final float scale;

    public PerlineNoise2(int octaves, float roughness, float scale) {
        this.octaves = octaves;
        this.roughness = roughness;
        this.scale = scale;
    }

    @Override
    public float[][] generate(Random rand, int worldWith, int worldHigh) {
        SimplexNoise.setRand(rand);
        float[][] totalNoise = new float[worldWith][worldHigh];
        float layerFrequency = scale;
        float layerWeight = 1;
        float weightSum = 0;

        // Summing up all octaves, the whole expression makes up a weighted average
        // computation where the noise with the lowest frequencies have the least effect
        for (int octave = 0; octave < octaves; octave++) {
            // Calculate single layer/octave of simplex noise, then add it to total noise
            for (int x = 0; x < worldWith; x++) {
                for (int y = 0; y < worldHigh; y++) {
                    totalNoise[x][y] += SimplexNoise.noise(x * layerFrequency, y * layerFrequency) * layerWeight;
                }
            }

            // Increase variables with each incrementing octave
            layerFrequency *= 2;
            weightSum += layerWeight;
            layerWeight *= roughness;

        }
        return totalNoise;
    }

    @Override
    public float get(int x, int y) {
        return 0.1f;
    }
}
