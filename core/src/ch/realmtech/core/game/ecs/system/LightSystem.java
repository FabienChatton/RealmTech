package ch.realmtech.core.game.ecs.system;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import ch.realmtech.core.game.ecs.plgin.SystemsAdminClient;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class LightSystem extends BaseSystem {
    @Wire(name = "physicWorld")
    private com.badlogic.gdx.physics.box2d.World physicWorld;
    @Wire(name = "gameCamera")
    private Camera gameCamera;

    @Wire(name = "gameStage")
    private Stage gameStage;
    @Wire
    private SystemsAdminClient systemsAdminClient;

    private RayHandler rayHandler;

    @Override
    protected void initialize() {
        rayHandler = new RayHandler(physicWorld);
        new PointLight(rayHandler, 360, new Color(1,1,1,1), 10, 0, 0);
    }

    @Override
    protected void processSystem() {
        float time = systemsAdminClient.timeSystemSimulation.getAccumulatedDelta();
        float alpha = (float) (Math.cos(Math.toRadians(time)) + 1) / 2f;

        gameStage.getBatch().begin();
        rayHandler.setCombinedMatrix(gameCamera.combined, 0, 0, gameCamera.viewportWidth, gameCamera.viewportHeight);
        rayHandler.setAmbientLight(new Color(0,0,0, alpha));
        rayHandler.setShadows(true);
        rayHandler.updateAndRender();
        gameStage.getBatch().end();
    }

    @Override
    protected void dispose() {
        rayHandler.dispose();
    }
}
