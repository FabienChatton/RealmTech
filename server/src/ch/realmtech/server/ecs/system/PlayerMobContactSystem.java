package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.Box2dComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ia.IaComponent;
import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

@All({PlayerConnexionComponent.class, PositionComponent.class, Box2dComponent.class})
public class PlayerMobContactSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<Box2dComponent> mBox2d;
    @Override
    protected void process(int entityId) {
        IntBag ias = world.getAspectSubscriptionManager().get(Aspect.all(IaComponent.class, PositionComponent.class, Box2dComponent.class)).getEntities();
        if (ias.isEmpty()) return;

        PositionComponent playerPositionComponent = mPos.get(entityId);
        Box2dComponent playerBox2dComponent = mBox2d.get(entityId);
        float grow = 1f;
        Rectangle.tmp.set(playerPositionComponent.x - grow / 2f, playerPositionComponent.y - grow / 2f, playerBox2dComponent.widthWorld + grow, playerBox2dComponent.heightWorld + grow);

        for (int i = 0; i < ias.size(); i++) {
            int ia = ias.get(i);
            PositionComponent iaPositionComponent = mPos.get(ia);
            Box2dComponent iaBox2dComponent = mBox2d.get(ia);
            Rectangle.tmp2.set(iaPositionComponent.x, iaPositionComponent.y, iaBox2dComponent.widthWorld, iaBox2dComponent.heightWorld);

            if (Rectangle.tmp.overlaps(Rectangle.tmp2)) {
                Vector2 playerRectangleCenter = new Vector2();
                Vector2 iaRectangleCenter = new Vector2();
                Rectangle.tmp.getCenter(playerRectangleCenter);
                Rectangle.tmp2.getCenter(iaRectangleCenter);
                Vector2 knockbackVector = playerRectangleCenter.sub(iaRectangleCenter).nor().scl(50f);

                playerBox2dComponent.body.applyLinearImpulse(knockbackVector, playerBox2dComponent.body.getWorldCenter(), true);
            }
        }
    }
}
