package ch.realmtech.server.level.worldGeneration;

import com.badlogic.gdx.math.MathUtils;

public class SeedGenerator {
    public static long randomSeed() {
        return MathUtils.random(Long.MIN_VALUE, Long.MAX_VALUE - 1);
    }
}
