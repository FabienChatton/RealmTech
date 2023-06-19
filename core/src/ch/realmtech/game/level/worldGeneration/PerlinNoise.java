package ch.realmtech.game.level.worldGeneration;

import java.util.Random;

public class PerlinNoise {
    private final Random rand;
    private final float[][] grid;
    private GeneratePerlinNoise perlinNoise;


    public PerlinNoise(Random rand, int worldWith, int worldHigh, GeneratePerlinNoise perlinNoise) {
        this.rand = rand;
        grid = perlinNoise.generate(rand,worldWith, worldHigh);
        this.perlinNoise = perlinNoise;
    }

    public float[][] getGrid() {
        return grid;
    }

    public float get(int x, int y) {
       return perlinNoise.get(x, y);
    }
}
