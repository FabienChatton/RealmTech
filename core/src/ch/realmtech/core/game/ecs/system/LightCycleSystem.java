package ch.realmtech.core.game.ecs.system;

import box2dLight.RayHandler;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class LightCycleSystem extends BaseSystem {
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
    private final Interpolation.Exp interpolation = new Interpolation.Exp(2, 10);

    @Override
    protected void processSystem() {
        float time = systemsAdminClient.getTimeSystemSimulation().getAccumulatedDelta();
        float alpha = Math.max(0.1f, interpolation.apply((float) (Math.sin(Math.toRadians(time * 3 / 10f) + 1f / 3f) + 1) / 2f));

        rayHandler.setCombinedMatrix(((OrthographicCamera) gameCamera));
        rayHandler.setAmbientLight(new Color(0,0,0, alpha));
        rayHandler.updateAndRender();
    }

    @Override
    protected void dispose() {
        rayHandler.dispose();
    }
}
