package ch.realmtechCommuns.level.worldGeneration;

public interface GetNoise {
    float getNoise(int x, int y, int octave, float roughness, float scale, SimplexNoise simplexNoise);
}
