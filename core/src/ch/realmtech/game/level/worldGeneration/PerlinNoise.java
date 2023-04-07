package ch.realmtech.game.level.worldGeneration;

import java.util.Random;

public class PerlinNoise {
    private final Random rand;
    private final float[][] grid;


    public PerlinNoise(Random rand, int worldWith, int worldHigh, GeneratePerlinNoise perlinNoise) {
        this.rand = rand;
        grid = perlinNoise.generate(rand,worldWith, worldHigh);
    }

    public float[][] getGrid() {
        return grid;
    }
}
