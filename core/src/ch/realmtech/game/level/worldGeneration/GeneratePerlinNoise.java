package ch.realmtech.game.level.worldGeneration;

import java.util.Random;

public interface GeneratePerlinNoise {
    float[][] generate(Random rand, int worldWith, int worldHigh);
}
