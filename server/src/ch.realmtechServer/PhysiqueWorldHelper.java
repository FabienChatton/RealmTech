package ch.realmtechServer;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class PhysiqueWorldHelper {
    public final static byte BIT_PLAYER = 1 << 1;
    public final static byte BIT_WORLD = 1 << 2;
    public final static byte BIT_GAME_OBJECT = 1 << 3;

    public static void resetBodyDef(BodyDef bodyDef) {
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);
        bodyDef.angle = 0;
        bodyDef.linearVelocity.set(0, 0);
        bodyDef.angularVelocity = 0;
        bodyDef.linearDamping = 0;
        bodyDef.angularDamping = 0;
        bodyDef.allowSleep = true;
        bodyDef.awake = true;
        bodyDef.fixedRotation = false;
        bodyDef.bullet = false;
        bodyDef.active = true;
        bodyDef.gravityScale = 1;
    }

    public static void resetFixtureDef(FixtureDef fixtureDef) {
        fixtureDef.shape = null;
        fixtureDef.friction = 0.2f;
        fixtureDef.restitution = 0;
        fixtureDef.density = 0;
        fixtureDef.isSensor = false;
        fixtureDef.filter.categoryBits = 0;
        fixtureDef.filter.maskBits = 0;
    }
}
