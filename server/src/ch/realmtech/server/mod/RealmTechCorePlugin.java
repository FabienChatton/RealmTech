package ch.realmtech.server.mod;

import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;

public class RealmTechCorePlugin implements ArtemisPlugin {

    @Override
    public void setup(WorldConfigurationBuilder b) {
        b.dependsOn(RealmTechCoreMod.class);
    }
}
