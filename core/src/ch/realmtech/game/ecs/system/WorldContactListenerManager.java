package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.ItemBeingPickComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;

public class WorldContactListenerManager extends Manager implements ContactListener {
    private final static String TAG = WorldContactListenerManager.class.getSimpleName();
    @Wire(name = "context")
    private RealmTech context;

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
        try {
            if (mPlayer.has((int) contact.getFixtureA().getBody().getUserData())) {
                int playerId = (int) contact.getFixtureA().getBody().getUserData();
                final Fixture fixtureB = contact.getFixtureB();
                if (mItem.has((int) fixtureB.getBody().getUserData())) {
                    contact.setEnabled(false);
                    int itemId = (int) fixtureB.getBody().getUserData();
                    Gdx.app.debug(TAG, "le joueur (" + playerId + ") a touche l'item (" + itemId + ")");
                    world.getSystem(ItemManager.class).playerPickUpItem(itemId, playerId);
                    world.getSystem(SoundManager.class).playItemPickUp();
                    Gdx.app.postRunnable(() -> {
                        try {
                            world.edit(itemId).remove(ItemBeingPickComponent.class);
                        } catch (NullPointerException e) {
                            Gdx.app.error(TAG, e.getMessage());
                        } finally {
                            context.physicWorld.destroyBody(fixtureB.getBody());
                        }
                    });
                }
            }
        } catch (NullPointerException e) {
            Gdx.app.error(TAG, e.getLocalizedMessage());
        }
    }
}
