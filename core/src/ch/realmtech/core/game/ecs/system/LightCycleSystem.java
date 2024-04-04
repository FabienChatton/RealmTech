package ch.realmtech.core.game.ecs.system;

import box2dLight.RayHandler;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.system.TimeSystem;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LightCycleSystem extends BaseSystem {
    private final static Logger logger = LoggerFactory.getLogger(LightCycleSystem.class);
    @Wire(name = "physicWorld")
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    @Wire(name = "gameCamera")
    private Camera gameCamera;

    @Wire(name = "gameStage")
    private Stage gameStage;
    @Wire
    private SystemsAdminClient systemsAdminClient;

    @Wire
    private RayHandler rayHandler;


    @Override
    protected void processSystem() {
        float time = systemsAdminClient.getTimeSystemSimulation().getAccumulatedDelta();
        float alpha = TimeSystem.getAlpha(time);

        rayHandler.setCombinedMatrix(((OrthographicCamera) gameCamera));
        rayHandler.setAmbientLight(new Color(0,0,0, alpha));
        try {
            rayHandler.updateAndRender();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }


    @Override
    protected void dispose() {
        rayHandler.dispose();
    }
}
