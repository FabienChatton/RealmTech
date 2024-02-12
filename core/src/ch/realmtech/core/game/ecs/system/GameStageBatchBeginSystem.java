package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class GameStageBatchBeginSystem extends BaseSystem {
    @Wire(name = "context")
    private RealmTech context;
    @Wire(name = "gameStage")
    private Stage gameStage;

    @Override
    protected void begin() {
        super.begin();
        gameStage.getBatch().setProjectionMatrix(gameStage.getCamera().combined);
        gameStage.getCamera().update();
        context.getGameStage().getBatch().begin();
    }

    @Override
    protected void processSystem() {

    }
}
