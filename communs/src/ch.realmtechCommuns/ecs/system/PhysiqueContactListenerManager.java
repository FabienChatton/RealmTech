package ch.realmtechCommuns.ecs.system;

import ch.realmtechCommuns.ecs.component.ItemBeingPickComponent;
import ch.realmtechCommuns.ecs.component.ItemComponent;
import ch.realmtechCommuns.ecs.component.PlayerComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class PhysiqueContactListenerManager extends Manager implements ContactListener {
    private final static String TAG = PhysiqueContactListenerManager.class.getSimpleName();
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
                    world.getSystem(ItemManager.class).playerPickUpItem(itemId, playerId);
                    //soundManager.playItemPickUp();
                    Gdx.app.postRunnable(() -> {
                        try {
                            world.edit(itemId).remove(ItemBeingPickComponent.class);
                        } catch (NullPointerException e) {
                            Gdx.app.error(TAG, e.getMessage());
                        } finally {
                            physicWorld.destroyBody(fixtureB.getBody());
                        }
                    });
                }
            }
        } catch (NullPointerException e) {
            Gdx.app.error(TAG, e.getLocalizedMessage());
        }
    }
}
