package ch.realmtech.server.enemy;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Box2dLocation implements Location<Vector2>{
    
    private final Body body;
    private float orientation;
    
    public Box2dLocation(Body body) {
        this.body = body;
        orientation = 0;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return Box2dSteeringUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return Box2dSteeringUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        body.setTransform(0, 0, body.getAngle());
        return new Box2dLocation(body);
    }
    
}