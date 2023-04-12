package ch.realmtech.game.mod;

import com.artemis.ArtemisPlugin;
import com.artemis.WorldConfigurationBuilder;

public class RealmTechCorePlugin implements ArtemisPlugin {

    @Override
    public void setup(WorldConfigurationBuilder b) {
        b.with(new RealmTechCoreMod());
    }
}
