package ch.realmtech.game.ecs.component;

import ch.realmtech.game.level.map.WorldMap;
import ch.realmtech.game.level.worldGeneration.PerlinNoise;
import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class WorldMapComponent extends Component {
    @EntityId
    public int saveId;
    public long seed;
    public PerlinNoise perlinNoise;
    public WorldMap worldMap;

}
