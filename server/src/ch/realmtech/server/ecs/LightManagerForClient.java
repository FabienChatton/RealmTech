package ch.realmtech.server.ecs;

import com.artemis.Manager;
import com.badlogic.gdx.graphics.Color;

public abstract class LightManagerForClient extends Manager {
    public abstract int createLight(int entityId, Color color, float distance, float x, float y);
    public abstract void disposeLight(int lightId);
}
