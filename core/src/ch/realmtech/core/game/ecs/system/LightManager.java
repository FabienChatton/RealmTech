package ch.realmtech.core.game.ecs.system;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import ch.realmtech.server.ecs.component.LightComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;

public class LightManager extends Manager {
    private ComponentMapper<LightComponent> mLight;
    @Wire
    private RayHandler rayHandler;

    public int createLight(Color color, float distance, float x, float y) {
        PointLight pointLight = new PointLight(rayHandler, 360, color, distance, x, y);
        int lightId = world.create();
        world.edit(lightId).create(LightComponent.class).set(pointLight);
        return lightId;
    }

    public void disposeLight(int lightId) {
        LightComponent lightComponent = mLight.get(lightId);
        lightComponent.getLight().dispose();
        world.delete(lightId);
    }
}
