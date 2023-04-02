package ch.realmtech.game.ecs.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.physics.box2d.World;

public class WorldStepSystem extends EntitySystem {
    private final World world;
    public WorldStepSystem(World world) {
        super(0);
        this.world = world;
    }

    @Override
    public void update(float deltaTime) {
        world.step(deltaTime, 6, 2);
    }
}
