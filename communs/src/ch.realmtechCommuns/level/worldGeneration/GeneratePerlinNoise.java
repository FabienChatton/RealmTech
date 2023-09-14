package ch.realmtechCommuns.level.worldGeneration;

import java.util.Random;

public interface GeneratePerlinNoise {
    float[][] generate(Random rand, int worldWith, int worldHigh);
    float get(int x, int y);
}
