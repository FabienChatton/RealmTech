package ch.realmtech.core.game.clickAndDrop;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.UuidComponent;
import ch.realmtech.server.ecs.system.InventoryManager;
import ch.realmtech.server.packet.serverPacket.MoveStackToStackPacket;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;

import java.util.UUID;

public class ClickAndDrop2 {
    private final RealmTech context;
    private final Stage stage;
    private final World world;
    private final ClickAndDropActor clickAndDropActor;
    private final Array<ClickAndDropActor> actors;
    private final Array<ClickAndDropActor> destinations;
    private final int playerId;

    public ClickAndDrop2(RealmTech context, Stage stage, World world, int playerId) {
        this.context = context;
        this.playerId = playerId;
        clickAndDropActor = new ClickAndDropActor(context, world.getSystem(InventoryManager.class).getCursorInventory(playerId).inventory[0], world.getMapper(ItemComponent.class), null) {
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
//                    ComponentMapper<ItemResultCraftComponent> mItemResult = world.getMapper(ItemResultCraftComponent.class);
//                    if (mItemResult.has(clickAndDropActorSrc.getStack()[0])) {
//                        if (button == Input.Buttons.LEFT || button == Input.Buttons.RIGHT) {
//                            if (world.getSystem(InventoryManager.class).canMouveStack(clickAndDropActorSrc.getStack(), clickAndDropActor.getStack())) {
//                                ItemResultCraftComponent itemResultCraftComponent = mItemResult.get(clickAndDropActorSrc.getStack()[0]);
//                                itemResultCraftComponent.pickEvent.pick(world);
//                                for (int i = 0; i < InventoryManager.tailleStack(clickAndDropActorSrc.getStack()); i++) {
//                                    world.edit(clickAndDropActorSrc.getStack()[i]).remove(ItemResultCraftComponent.class);
//                                }
//                            }
//                        }
//                    }
                    if (button == Input.Buttons.LEFT) {
                        ClickAndDrop2.this.moveStackToStackSendRequest(clickAndDropActorSrc.getStack(), clickAndDropActor.getStack());
                    } else if (button == Input.Buttons.RIGHT) {
                        int nombreADeplacer;
                        if (InventoryManager.tailleStack(clickAndDropActorSrc.getStack()) == 1) {
                            nombreADeplacer = 1;
                        } else {
                            nombreADeplacer = InventoryManager.tailleStack(clickAndDropActorSrc.getStack()) / 2;
                        }
                        ClickAndDrop2.this.moveStackToStackNumberSendRequest(clickAndDropActorSrc.getStack(), clickAndDropActor.getStack(), nombreADeplacer);
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
        ComponentMapper<InventoryComponent> mInventory = context.getEcsEngine().getWorld().getMapper(InventoryComponent.class);
        ComponentMapper<PlayerConnexionComponent> mPlayerConnexion = context.getEcsEngine().getWorld().getMapper(PlayerConnexionComponent.class);
        final ClickAndDropListener listener = new ClickAndDropListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.isStopped()) return false;
                boolean ret = false;
                if (button == Input.Buttons.LEFT) {
                    ClickAndDrop2.this.moveStackToStackSendRequest(clickAndDropActor.getStack(), clickAndDropActorDst.getStack());
                    ret = true;
                } else if (button == Input.Buttons.RIGHT) {
                    ClickAndDrop2.this.moveStackToStackNumberSendRequest(clickAndDropActor.getStack(), clickAndDropActorDst.getStack(), 1);
                    ret = true;
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

    public void moveStackToStackSendRequest(int[] srcStack, int[] dstStack) {
        moveStackToStackNumberSendRequest(srcStack, dstStack, InventoryManager.tailleStack(srcStack));
    }

    public void moveStackToStackNumberSendRequest(int[] srcStack, int[] dstStack, int number) {
        ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
        ComponentMapper<UuidComponent> mUuid = world.getMapper(UuidComponent.class);
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);

        int srcInventoryId = world.getSystem(InventoryManager.class).getInventoryByStack(srcStack);
        int dstInventoryId = world.getSystem(InventoryManager.class).getInventoryByStack(dstStack);
        UUID[] srcItemsUuid = new UUID[number];

        InventoryComponent srcInventoryComponent = mInventory.get(srcInventoryId);
        InventoryComponent dstInventoryComponent = mInventory.get(dstInventoryId);

        for (int i = 0; i < srcItemsUuid.length; i++) {
            srcItemsUuid[i] = mUuid.get(srcStack[i]).getUuid();
        }
        int dstIndex = -1;
        for (int i = 0; i < dstInventoryComponent.inventory.length; i++) {
            if (dstInventoryComponent.inventory[i] == dstStack) {
                dstIndex = i;
            }
        }

        context.getConnexionHandler().sendAndFlushPacketToServer(
                new MoveStackToStackPacket(mUuid.get(srcInventoryId).getUuid(), mUuid.get(dstInventoryId).getUuid(), srcItemsUuid, dstIndex)
        );
    }
}
