package ch.realmtech.game.clickAndDrop.clickAndDrop2;

import ch.realmtech.game.ecs.component.InventoryComponent;
import ch.realmtech.game.ecs.component.ItemComponent;
import ch.realmtech.game.ecs.system.InventoryManager;
import com.artemis.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class ClickAndDrop2 {
    private final Stage stage;
    private final World world;
    private final ClickAndDropActor clickAndDropActor;
    private final Array<ClickAndDropActor> clickAndDropActorsDst;

    public ClickAndDrop2(Stage stage, World world) {
        clickAndDropActor = new ClickAndDropActor(new int[InventoryComponent.DEFAULT_STACK_LIMITE], world.getMapper(ItemComponent.class), null) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (clickAndDropActor.getStack()[0] == 0) {
                    clickAndDropActor.setWidth(0);
                    clickAndDropActor.setHeight(0);
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
        clickAndDropActor.setZIndex(1);
        this.stage = stage;
        this.world = world;
        clickAndDropActorsDst = new Array<>();
    }

    public void addSource(final ClickAndDropActor clickAndDropActorSrc) {
        final ClickAndDropListener listener = new ClickAndDropListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // prendre de la source si l'inventaire de l'acteur focus est vide
                if (InventoryManager.tailleStack(clickAndDropActor.getStack()) == 0) {
                    world.getSystem(InventoryManager.class).moveStackToStack(clickAndDropActorSrc.getStack(), clickAndDropActor.getStack());
                    event.stop();
                    return true;
                }
                return false;
            }
        };
        clickAndDropActorSrc.addCaptureListener(listener);
    }

    public void addDestination(ClickAndDropActor clickAndDropActorDst) {
        clickAndDropActorsDst.add(clickAndDropActorDst);
        final ClickAndDropListener listener = new ClickAndDropListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (InventoryManager.tailleStack(clickAndDropActor.getStack()) != 0 && !event.isStopped()) {
                    world.getSystem(InventoryManager.class).moveStackToStack(clickAndDropActor.getStack(), clickAndDropActorDst.getStack());
                    return true;
                }
                return false;
            }
        };
        clickAndDropActorDst.addCaptureListener(listener);
    }
}
