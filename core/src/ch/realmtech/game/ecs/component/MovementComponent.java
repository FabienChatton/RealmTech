package ch.realmtech.game.ecs.component;

import ch.realmtech.game.ecs.PoolableComponent;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent implements PoolableComponent {
    public Vector2 speed;
    public float maxSpeedUnite;
    public float speedMeterParSeconde;

    public MovementComponent() {
        speed = new Vector2();
    }

    public void init(float maxSpeedUnite, float speedMeterParSeconde){
        this.maxSpeedUnite = maxSpeedUnite;
        this.speedMeterParSeconde = speedMeterParSeconde;
    }

    @Override
    public void reset() {
        speedMeterParSeconde = 0;
        maxSpeedUnite = 0;
        speed.set(0,0);
    }
}
