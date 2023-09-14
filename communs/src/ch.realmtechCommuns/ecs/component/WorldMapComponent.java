package ch.realmtechCommuns.ecs.component;

import ch.realmtechCommuns.level.map.WorldMap;
import ch.realmtechCommuns.level.worldGeneration.PerlinNoise;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class WorldMapComponent extends Component {
    @EntityId
    public int saveId;
    public long seed;
    public PerlinNoise perlinNoise;
    public WorldMap worldMap;

}
