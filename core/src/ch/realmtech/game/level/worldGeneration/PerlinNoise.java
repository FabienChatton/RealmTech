package ch.realmtech.game.level.worldGeneration;

import java.util.Random;

public class PerlinNoise {
    private final Random rand;
    private GetNoise perlinNoise;


    public PerlinNoise(Random rand, GetNoise perlinNoise) {
        this.rand = rand;
        this.perlinNoise = perlinNoise;
    }


    public float get(int x, int y) {
       return perlinNoise.getNoise(x, y);
    }
}
