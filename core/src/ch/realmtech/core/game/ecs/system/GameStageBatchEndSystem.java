package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;

public class GameStageBatchEndSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    @Override
    protected void processSystem() {

    }

    @Override
    protected void end() {
        super.end();
        context.getGameStage().getBatch().end();
    }
}
