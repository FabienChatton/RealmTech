package ch.realmtechServer.ecs.system;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.component.PlayerConnexionComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.physics.box2d.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhysiqueContactListenerManager extends Manager implements ContactListener {
    private final static Logger logger = LoggerFactory.getLogger(PhysiqueContactListenerManager.class);
//    @Wire
//    private SoundManager soundManager;
    @Wire(name = "physicWorld")
    private World physicWorld;
    @Wire
    private ItemManagerServer itemManager;

    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;

    private ComponentMapper<ItemComponent> mItem;

    @Override
    public void beginContact(Contact contact) {
        playerPickUpItem(contact);

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void playerPickUpItem(final Contact contact) {
        if (contact.getFixtureA().getBody().getUserData() == null || contact.getFixtureB().getBody().getUserData() == null) {
            return;
        }
        try {
            if (mPlayerConnexion.has((int) contact.getFixtureA().getBody().getUserData())) {
                int playerId = (int) contact.getFixtureA().getBody().getUserData();
                final Fixture fixtureB = contact.getFixtureB();
                if (mItem.has((int) fixtureB.getBody().getUserData())) {
                    int itemId = (int) fixtureB.getBody().getUserData();
                    //soundManager.playItemPickUp();
                    ServerContext.nextTick(() -> {
                        try {
                                itemManager.playerPickUpItem(itemId, playerId);
                        } catch (NullPointerException e) {
                            logger.error(e.getMessage(), e);
                        } finally {
                            physicWorld.destroyBody(fixtureB.getBody());
                        }
                    });
                }
            }
        } catch (NullPointerException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
