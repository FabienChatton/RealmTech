package ch.realmtech.game.clickAndDrop;

import ch.realmtech.RealmTech;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public final class ClickAndDrop {
    private ClickActorAndSlot actor;
    private final Array<ClickActorAndSlot> sources;
    private final Array<ClickActorAndSlot> targets;
    private final RealmTech context;
    private Subcriber<int[]> mouseSubscriber;
    private Subcriber<InputMapper.PointerMapper> pointerSubcriber;

    public ClickAndDrop(RealmTech context) {
        sources = new Array<>();
        targets = new Array<>();
        this.context = context;
    }

    public void addSource(final ClickActorAndSlot source, final ClickAndDropEvent clickAndDropEvent) {
        sources.add(source);
        source.actor.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (actor == null) {
                    if (clickAndDropEvent.clickStart(source)) {
                        actor = source;
                        final Stage stage = event.getStage();
                        Gdx.input.setInputProcessor(context.getInputManager());
                        mouseSubscriber = objectToNotify -> {
                            final Vector3 unproject = stage.getCamera().unproject(new Vector3(objectToNotify[0], objectToNotify[1], 0));
                            source.actor.setPosition(unproject.x - source.actor.getWidth() / 2, unproject.y - source.actor.getHeight() / 2);
                        };
                        source.actor.setTouchable(Touchable.disabled);
                        mouseSubscriber.receive(new int[]{Gdx.input.getX(), Gdx.input.getY()});
                        pointerSubcriber = new Subcriber<>() {
                            @Override
                            public void receive(InputMapper.PointerMapper objectToNotify) {
                                final Vector3 unproject = stage.getCamera().unproject(new Vector3(objectToNotify.lastTouchedScreenX, objectToNotify.lastTouchedScreenY, 0));
                                final Actor actorDst = source.actor.getStage().hit(unproject.x, unproject.y, true);
                                ClickActorAndSlot clickActorAndSlot = null;
                                if (actorDst != null) {
                                    for (ClickActorAndSlot target : targets) {
                                        if (target.actor.isAscendantOf(actorDst)) {
                                            clickActorAndSlot = target;
                                            break;
                                        }
                                    }
                                }
                                if (clickAndDropEvent.clickStop(source, clickActorAndSlot)) {
                                    stage.getActors().removeValue(source.actor, true);
                                    context.getInputManager().pointerSignal.remove(this);
                                    actor = null;
                                    Gdx.input.setInputProcessor(stage);
                                }
                            }
                        };
                        stage.addActor(source.actor);
                        context.getInputManager().mouseMove.add(mouseSubscriber);
                        context.getInputManager().pointerSignal.add(pointerSubcriber);
                    }
                }
            }
        });
    }

    public void addTarget(ClickActorAndSlot clickActorAndSlot) {
        targets.add(clickActorAndSlot);
    }
    public void clear() {
        clearSources();
        clearTarges();
        if (actor != null) actor.actor.remove();
        if (pointerSubcriber != null) context.getInputManager().pointerSignal.remove(pointerSubcriber);
        actor = null;
        pointerSubcriber = null;
    }
    public void clearSources() {
        sources.clear();
    }

    public void clearTarges() {
        targets.clear();
    }
}
