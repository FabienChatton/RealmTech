package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.ItemBeingPickComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.PlayerComponent;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListenerManager extends Manager implements ContactListener {
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
                if (mItem.has((int) contact.getFixtureB().getBody().getUserData())) {
                    contact.setEnabled(false);
                    int itemId = (int) contact.getFixtureB().getBody().getUserData();
                    world.getSystem(ItemManager.class).playerPickUpItem(itemId, playerId);
                    Gdx.app.postRunnable(() -> {
                        try {
                            world.edit(itemId).remove(ItemBeingPickComponent.class);
                        } catch (NullPointerException e) {
                        } finally {
                            context.physicWorld.destroyBody(contact.getFixtureB().getBody());

                        }
                    });
                }
            }
        } catch (NullPointerException e) {
            // sa va trop vite, surement
        }
    }
}
