package ch.realmtech.game.ecs.component;

import ch.realmtech.game.ecs.PoolableComponent;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements PoolableComponent {
    public Vector2 speed;
    public float maxSpeedUnite;
    public float speedMeterParSeconde;

    public MovementComponent(float speedMeterParSeconde, float maxSpeedUnite) {
        this.speedMeterParSeconde = speedMeterParSeconde;
        this.maxSpeedUnite = maxSpeedUnite;
        speed = new Vector2();
    }

    @Override
    public void reset() {
        speedMeterParSeconde = 0;
        maxSpeedUnite = 0;
        speed.set(0,0);
    }
}
