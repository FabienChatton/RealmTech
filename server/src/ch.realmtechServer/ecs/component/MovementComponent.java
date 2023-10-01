package ch.realmtechServer.ecs.component;


import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class MovementComponent extends Component {
    public Vector2 speed;
    public float maxSpeedUnite;
    public float speedMeterParSeconde;

    public MovementComponent() {
        speed = new Vector2();
    }

    public void set(float maxSpeedUnite, float speedMeterParSeconde){
        this.maxSpeedUnite = maxSpeedUnite;
        this.speedMeterParSeconde = speedMeterParSeconde;
    }
}