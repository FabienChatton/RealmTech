package ch.realmtechServer.ecs.system;

import ch.realmtechServer.ServerContext;
import ch.realmtechServer.ctrl.ItemManagerCommun;
import ch.realmtechServer.ecs.component.ItemBeingPickComponent;
import ch.realmtechServer.ecs.component.ItemComponent;
import ch.realmtechServer.ecs.component.PlayerComponent;
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

    private ComponentMapper<PlayerComponent> mPlayer;

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
            if (mPlayer.has((int) contact.getFixtureA().getBody().getUserData())) {
                int playerId = (int) contact.getFixtureA().getBody().getUserData();
                final Fixture fixtureB = contact.getFixtureB();
                if (mItem.has((int) fixtureB.getBody().getUserData())) {
                    contact.setEnabled(false);
                    int itemId = (int) fixtureB.getBody().getUserData();
                    ItemManagerCommun.playerPickUpItem(world, itemId, playerId);
                    //soundManager.playItemPickUp();
                    ServerContext.nextTick(() -> {
                        try {
                            world.edit(itemId).remove(ItemBeingPickComponent.class);
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
