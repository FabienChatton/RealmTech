package ch.realmtech.game.ecs.component;

import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import com.artemis.PooledComponent;

public class InfMetaDonneesComponent extends PooledComponent {
    public long seed;
    public float playerPositionX;
    public float playerPositionY;
    public PerlinNoise perlinNoise;

    public InfMetaDonneesComponent set(long seed, float playerPositionX, float playerPositionY) {
        this.seed = seed;
        this.playerPositionX = playerPositionX;
        this.playerPositionY = playerPositionY;
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
