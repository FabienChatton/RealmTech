package ch.realmtech.server.level.worldGeneration;

public interface GetNoise {
    float getNoise(int x, int y, int octave, float roughness, float scale, SimplexNoise simplexNoise);
}
