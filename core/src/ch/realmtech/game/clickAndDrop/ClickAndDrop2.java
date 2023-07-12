package ch.realmtech.game.clickAndDrop;

import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.component.ItemResultCraftComponent;
import ch.realmtech.game.ecs.system.InventoryManager;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.managers.TagManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;

public class ClickAndDrop2 {
    private final Stage stage;
    private final World world;
    private final ClickAndDropActor clickAndDropActor;
    private final Array<ClickAndDropActor> actors;

    public ClickAndDrop2(Stage stage, World world) {
        clickAndDropActor = new ClickAndDropActor(new int[InventoryComponent.DEFAULT_STACK_LIMITE], world.getMapper(ItemComponent.class), null) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (clickAndDropActor.getStack()[0] == 0) {
                    clickAndDropActor.setWidth(0);
                    clickAndDropActor.setHeight(0);
                    clickAndDropActor.setZIndex(200);
                }
            }
        };
        stage.addCaptureListener(new InputListener() {
            @Override
            public boolean mouseMoved(InputEvent event, float stageX, float stageY) {
                clickAndDropActor.setBounds(stageX - clickAndDropActor.getWidth() / 2, stageY - clickAndDropActor.getHeight() / 2, clickAndDropActor.getWidth(), clickAndDropActor.getHeight());
                return true;
            }
        });
        stage.addActor(clickAndDropActor);
        clickAndDropActor.setTouchable(Touchable.disabled);
        this.stage = stage;
        this.world = world;
        this.actors = new Array<>(50);
    }

    public void addSource(final ClickAndDropActor clickAndDropActorSrc) {
        final ClickAndDropListener listener = new ClickAndDropListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (InventoryManager.tailleStack(clickAndDropActor.getStack()) == 0) {
                    if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
                        final ComponentMapper<ItemResultCraftComponent> mItemResult = world.getMapper(ItemResultCraftComponent.class);
                        if (mItemResult.has(clickAndDropActorSrc.getStack()[0])) {
                            mItemResult.get(clickAndDropActorSrc.getStack()[0]).pickEvent.pick(world, world.getMapper(InventoryComponent.class).get(world.getSystem(TagManager.class).getEntityId("crafting")));
                            for (int i = 0; i < InventoryManager.tailleStack(clickAndDropActorSrc.getStack()); i++) {
                                world.edit(clickAndDropActorSrc.getStack()[i]).remove(ItemResultCraftComponent.class);
                            }
                        }
                    }
                    if (button == Input.Buttons.LEFT) {
                        world.getSystem(InventoryManager.class).moveStackToStack(clickAndDropActorSrc.getStack(), clickAndDropActor.getStack());
                    } else if (button == Input.Buttons.RIGHT) {
                        int nombreADeplacer;
                        if (InventoryManager.tailleStack(clickAndDropActorSrc.getStack()) == 1) {
                             nombreADeplacer = 1;
                        } else {
                            nombreADeplacer = InventoryManager.tailleStack(clickAndDropActorSrc.getStack()) / 2;
                        }
                        world.getSystem(InventoryManager.class).moveStackToStackNumber(clickAndDropActorSrc.getStack(), clickAndDropActor.getStack(), nombreADeplacer);
                    }
                    clickAndDropActor.act(Gdx.graphics.getDeltaTime());
                    event.getStage().mouseMoved(Gdx.input.getX(), Gdx.input.getY());
                    event.stop();
                    return true;
                }
                return false;
            }
        };
        clickAndDropActorSrc.addCaptureListener(listener);
        actors.add(clickAndDropActorSrc);
    }

    public void addDestination(ClickAndDropActor clickAndDropActorDst) {
        final ClickAndDropListener listener = new ClickAndDropListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.isStopped()) return false;
                if (button == Input.Buttons.LEFT) {
                    world.getSystem(InventoryManager.class).moveStackToStack(clickAndDropActor.getStack(), clickAndDropActorDst.getStack());
                    return true;
                } else if (button == Input.Buttons.RIGHT) {
                    world.getSystem(InventoryManager.class).moveStackToStackNumber(clickAndDropActor.getStack(), clickAndDropActorDst.getStack(), 1);
                    return true;
                }
                return false;
            }
        };
        clickAndDropActorDst.addCaptureListener(listener);
    }

    public void clearActor() {
        for (ClickAndDropActor actor : actors) {
            stage.getActors().removeValue(actor, true);
        }
        actors.clear();
    }
}
