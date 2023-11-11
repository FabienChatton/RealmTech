package ch.realmtech.server.ecs.component;

import ch.realmtech.server.level.worldGeneration.PerlinNoise;
import ch.realmtech.server.level.worldGeneration.PerlineNoise3;
import com.artemis.PooledComponent;
import com.artemis.World;

import java.util.Random;

public class InfMetaDonneesComponent extends PooledComponent {
    public long seed;
    public float playerPositionX;
    public float playerPositionY;
    public PerlinNoise perlinNoise;
    public String saveName;

    public InfMetaDonneesComponent set(long seed, float playerPositionX, float playerPositionY, String saveName, World world) {
        Random rand = new Random(seed);
        this.perlinNoise = new PerlinNoise(rand, new PerlineNoise3(seed), world);
        this.seed = seed;
        this.playerPositionX = playerPositionX;
        this.playerPositionY = playerPositionY;
        this.saveName = saveName;
        return this;
    }

    @Override
    protected void reset() {
        seed = -0;
        playerPositionX = 0;
        playerPositionY = 0;
        perlinNoise = null;
    }
}
