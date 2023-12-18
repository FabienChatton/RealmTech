package ch.realmtech.core.game.ecs.system;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import ch.realmtech.server.ecs.LightManagerForClient;
import ch.realmtech.server.ecs.component.LightComponent;
import com.artemis.ComponentMapper;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.Color;

public class LightManager extends LightManagerForClient {
    private ComponentMapper<LightComponent> mLight;
    @Wire
    private RayHandler rayHandler;

    public int createLight(int entityId, Color color, float distance, float x, float y) {
        PointLight pointLight = new PointLight(rayHandler, 360, color, distance, x + 0.5f, y + 0.5f);
        world.edit(entityId).create(LightComponent.class).set(pointLight);
        return entityId;
    }

    public void disposeLight(int entityId) {
        LightComponent lightComponent = mLight.get(entityId);
        lightComponent.getLight().remove();
    }
}
