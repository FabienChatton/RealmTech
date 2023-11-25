package ch.realmtech.server.ecs.component;

import ch.realmtech.server.level.worldGeneration.PerlinNoise;
import ch.realmtech.server.level.worldGeneration.PerlineNoise3;
import com.artemis.Component;
import com.artemis.World;

import java.util.Random;

public class SaveMetadataComponent extends Component {
    public long seed;
    public PerlinNoise perlinNoise;
    public String saveName;

    public SaveMetadataComponent set(long seed, String saveName, World world) {
        Random rand = new Random(seed);
        this.perlinNoise = new PerlinNoise(rand, new PerlineNoise3(seed), world);
        this.seed = seed;
        this.saveName = saveName;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SaveMetadataComponent testSaveMetadataComponent) {
            return seed == testSaveMetadataComponent.seed && saveName.equals(testSaveMetadataComponent.saveName);
        }
        return false;
    }
}
