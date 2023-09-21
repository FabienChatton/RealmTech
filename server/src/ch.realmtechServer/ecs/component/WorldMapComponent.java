package ch.realmtechServer.ecs.component;

import ch.realmtechServer.level.map.WorldMap;
import ch.realmtechServer.level.worldGeneration.PerlinNoise;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class WorldMapComponent extends Component {
    @EntityId
    public int saveId;
    public long seed;
    public PerlinNoise perlinNoise;
    public WorldMap worldMap;

}
