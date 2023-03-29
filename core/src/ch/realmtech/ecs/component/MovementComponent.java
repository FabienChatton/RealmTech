package ch.realmtech.ecs.component;

import ch.realmtech.ecs.PoolableComponent;

public class MovementComponent implements PoolableComponent {
    public float speedUnite;

    public MovementComponent(float speedUniteParSeconde) {
        this.speedUnite = speedUniteParSeconde;
    }

    @Override
    public void reset() {
        speedUnite = 0;
    }
}
