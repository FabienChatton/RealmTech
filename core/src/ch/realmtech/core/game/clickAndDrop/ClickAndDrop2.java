package ch.realmtech.core.game.clickAndDrop;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.ItemResultCraftComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.packet.serverPacket.PlayerInventorySetRequestPacket;
import ch.realmtech.server.serialize.inventory.InventorySerializer;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;

public class ClickAndDrop2 {
    private final RealmTech context;
    private final Stage stage;
    private final World world;
    private final ClickAndDropActor clickAndDropActor;
    private final Array<ClickAndDropActor> actors;
    private final Array<ClickAndDropActor> destinations;

    public ClickAndDrop2(RealmTech context, Stage stage, World world) {
        this.context = context;
        clickAndDropActor = new ClickAndDropActor(context, new int[InventoryComponent.DEFAULT_STACK_LIMITE], world.getMapper(ItemComponent.class), null) {
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
        this.destinations = new Array<>(50);
    }

    // prend un item
    public void addSource(final ClickAndDropActor clickAndDropActorSrc) {
        final ClickAndDropListener listener = new ClickAndDropListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (InventoryManager.tailleStack(clickAndDropActor.getStack()) == 0 || !destinations.contains(clickAndDropActorSrc, true)) {
                    ComponentMapper<ItemResultCraftComponent> mItemResult = world.getMapper(ItemResultCraftComponent.class);
                    if (mItemResult.has(clickAndDropActorSrc.getStack()[0])) {
                        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
                            if (world.getSystem(InventoryManager.class).canMouveStack(clickAndDropActorSrc.getStack(), clickAndDropActor.getStack())) {
                                ItemResultCraftComponent itemResultCraftComponent = mItemResult.get(clickAndDropActorSrc.getStack()[0]);
                                itemResultCraftComponent.pickEvent.pick(world);
                                for (int i = 0; i < InventoryManager.tailleStack(clickAndDropActorSrc.getStack()); i++) {
                                    world.edit(clickAndDropActorSrc.getStack()[i]).remove(ItemResultCraftComponent.class);
                                }
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

    // depose un item
    public void addDestination(ClickAndDropActor clickAndDropActorDst) {
        final ClickAndDropListener listener = new ClickAndDropListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.isStopped()) return false;
                boolean ret = false;
                if (button == Input.Buttons.LEFT) {
                    world.getSystem(InventoryManager.class).moveStackToStack(clickAndDropActor.getStack(), clickAndDropActorDst.getStack());
                    ret = true;
                } else if (button == Input.Buttons.RIGHT) {
                    world.getSystem(InventoryManager.class).moveStackToStackNumber(clickAndDropActor.getStack(), clickAndDropActorDst.getStack(), 1);
                    ret = true;
                }
                if (ret) {
                    InventoryComponent inventoryComponent = context.getEcsEngine().getPlayerEntity().getComponent(InventoryComponent.class);
                    context.getConnexionHandler().sendAndFlushPacketToServer(new PlayerInventorySetRequestPacket(InventorySerializer.toBytes(world, inventoryComponent)));
                }
                return ret;
            }
        };
        clickAndDropActorDst.addCaptureListener(listener);
        destinations.add(clickAndDropActorDst);
    }

    public void clearActor() {
        for (ClickAndDropActor actor : actors) {
            stage.getActors().removeValue(actor, true);
        }
        actors.clear();
        destinations.clear();
    }

    public Array<ClickAndDropActor> getActors() {
        return actors;
    }
}