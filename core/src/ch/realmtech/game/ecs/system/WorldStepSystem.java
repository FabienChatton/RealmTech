package ch.realmtech.game.ecs.system;

import com.artemis.EntitySystem;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;

@Exclude
public class WorldStepSystem extends EntitySystem {
    @Wire(name = "physicWorld")
    private World physicWorld;

    @Override
    protected void processSystem() {
        physicWorld.step(Gdx.graphics.getDeltaTime(), 6, 2);
    }
}
