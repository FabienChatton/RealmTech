package ch.realmtech.game.clickAndDrop;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.system.InventoryManager;
import ch.realmtech.game.ecs.system.PlayerInventorySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public final class ClickAndDrop implements InputProcessor {
    /** La source du click */
    private ClickActorAndSlot source;

    /** l'acteur affiché */
    private ImageItemTable actorAffiche;

    /** l'event de l'acteur*/
    private ClickAndDropEvent clickAndDropEvent;

    /** "l'inventaire temporaire" */
    private int[] stackActive;

    /** Toutes les targets disponibles pour le drop de la source */
    private final Array<ClickActorAndSlot> targets;
    private final RealmTech context;

    public ClickAndDrop(RealmTech context) {
        targets = new Array<>();
        this.context = context;
        this.stackActive = new int[InventoryComponent.DEFAULT_STACK_LIMITE];
    }

    public void addSource(final ClickActorAndSlot source, final ClickAndDropEvent clickAndDropEvent) {
        ClickListener clickListener = new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (ClickAndDrop.this.source == null) {
                    ClickAndDrop.this.source = source;
                    ClickAndDrop.this.clickAndDropEvent = clickAndDropEvent;
                    source.actor.getListeners().removeValue(this, true);
                    actorAffiche = clickAndDropEvent.clickStart(source, stackActive, event);
                    if (actorAffiche != null) {
                        final Stage stage = event.getStage();
                        // pour faire bouge l'item avec la souris
                        Gdx.input.setInputProcessor(ClickAndDrop.this);
                        stage.addActor(actorAffiche);
                        ClickAndDrop.this.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
                    }
                }
                return true;
            }
        };
        source.actor.addListener(clickListener);
    }

    public void addTarget(ClickActorAndSlot clickActorAndSlot) {
        targets.add(clickActorAndSlot);
    }

    public void clear() {
        if (source != null) {
            source.actor.remove();
            resetStack();
            source = null;
        }
        if (actorAffiche != null) {
            actorAffiche.remove();
            actorAffiche = null;
        }
        clickAndDropEvent = null;
        clearTarges();
    }

    public void resetStack() {
        context.getEcsEngine().getWorld().getSystem(InventoryManager.class).moveStackToStack(stackActive, source.getStack());
    }

    public void clearTarges() {
        targets.clear();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        final Vector3 uproject = actorAffiche.getStage().getCamera().unproject(new Vector3(screenX, screenY, 0));
        final Stage stage = source.actor.getStage();
        actorAffiche.getImage().remove();
        final Actor actorDst = stage.hit(uproject.x, uproject.y, false);
        ClickActorAndSlot clickActorAndSlot = null;
        if (actorDst != null) {
            for (ClickActorAndSlot target : targets) {
                if (actorDst.isAscendantOf(target.actor)) {
                    clickActorAndSlot = target;
                    break;
                }
                if (actorDst.isDescendantOf(target.actor)) {
                    clickActorAndSlot = target;
                    break;
                }
            }
        }
        // stop le click
        final ImageItemTable imageItemTable = clickAndDropEvent.clickStop(source, stackActive, clickActorAndSlot, button);
        if (imageItemTable == null) {
            Gdx.input.setInputProcessor(source.actor.getStage());
            context.getEcsEngine().getWorld().getSystem(PlayerInventorySystem.class).refreshPlayerInventory();
        } else {
            actorAffiche.getStage().addActor(imageItemTable);
            actorAffiche.remove();
            actorAffiche = imageItemTable;
        }
        ClickAndDrop.this.mouseMoved(Gdx.input.getX(), Gdx.input.getY());
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (actorAffiche != null) {
            final Vector3 uproject = actorAffiche.getStage().getCamera().unproject(new Vector3(screenX, screenY, 0));
            actorAffiche.setPosition(uproject.x, uproject.y);
            context.getUiStage().getBatch().begin();
            if (actorAffiche.getImage() != null) {
                actorAffiche.getImage().getDrawable().draw(context.getUiStage().getBatch(), screenX, screenY, actorAffiche.getWidth(), actorAffiche.getHeight());
            }
            context.getUiStage().getBatch().end();
        }
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
