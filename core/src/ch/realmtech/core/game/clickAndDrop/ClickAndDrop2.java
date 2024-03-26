package ch.realmtech.core.game.clickAndDrop;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.InventoryComponent;
import ch.realmtech.server.ecs.component.ItemComponent;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class ClickAndDrop2 {
    private final static Logger logger = LoggerFactory.getLogger(ClickAndDrop2.class);
    private final RealmTech context;
    private final Stage stage;
    private final World world;
    private final ClickAndDropActor clickAndDropActor;
    private final Array<ClickAndDropActor> actors;
    private final Array<ClickAndDropActor> destinations;
    private final int playerId;
    private final SystemsAdminClient systemsAdminClient;

    public ClickAndDrop2(RealmTech context, Stage stage, World world, int playerId, SystemsAdminClient systemsAdminClient) {
        this.context = context;
        this.playerId = playerId;
        this.systemsAdminClient = systemsAdminClient;
        int cursorInventoryId = context.getSystemsAdminClient().getInventoryManager().getCursorInventoryId(playerId);
        InventoryComponent inventoryCursorComponent = world.getMapper(InventoryComponent.class).get(cursorInventoryId);
        UUID cursorInventoryUuid = systemsAdminClient.getUuidEntityManager().getEntityUuid(cursorInventoryId);
        clickAndDropActor = new ClickAndDropActor(context, cursorInventoryUuid, () -> inventoryCursorComponent.inventory[0], null, null) {
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
                    if (button == Input.Buttons.LEFT) {
                        moveLeftClickStackToStack(clickAndDropActorSrc, clickAndDropActor);
                    } else if (button == Input.Buttons.RIGHT) {
                        int nombreADeplacer;
                        if (InventoryManager.tailleStack(clickAndDropActorSrc.getStack()) == 1) {
                            nombreADeplacer = 1;
                        } else {
                            nombreADeplacer = InventoryManager.tailleStack(clickAndDropActorSrc.getStack()) / 2;
                        }
                        moveRightStackToStackNumber(clickAndDropActorSrc, clickAndDropActor, nombreADeplacer);

                    }
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

    private void moveLeftClickStackToStack(ClickAndDropActor srcActor, ClickAndDropActor dstActor) {
        if (InventoryManager.tailleStack(srcActor.getStack()) > 0) {
            ClickAndDrop2.this.moveStackToStackSendRequest(srcActor, dstActor);
        }
    }

    private void moveRightStackToStackNumber(ClickAndDropActor srcActor, ClickAndDropActor dstActor, int number) {
        ClickAndDrop2.this.moveStackToStackNumberSendRequest(srcActor, dstActor, number);
    }

    // depose un item
    public void addDestination(ClickAndDropActor clickAndDropActorDst) {
        ComponentMapper<ItemComponent> mItem = context.getEcsEngine().getWorld().getMapper(ItemComponent.class);
        final ClickAndDropListener listener = new ClickAndDropListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.isStopped()) return false;
                if (clickAndDropActorDst.getDstRequirePredicate() != null) {
                    InventoryComponent cursorItem = context.getSystemsAdminClient().getInventoryManager().getCursorInventory(playerId);
                    ItemComponent cursorTopItem = mItem.get(context.getSystemsAdminClient().getInventoryManager().getTopItem(cursorItem.inventory[0]));
                    // stop if clickAndDropActorDst is not true
                    if (!clickAndDropActorDst.getDstRequirePredicate().test(ClickAndDrop2.this.systemsAdminClient, cursorTopItem.itemRegisterEntry)) {
                        return false;
                    }
                }
                boolean ret = false;
                if (button == Input.Buttons.LEFT) {
                    ClickAndDrop2.this.moveStackToStackSendRequest(clickAndDropActor, clickAndDropActorDst);
                    ret = true;
                } else if (button == Input.Buttons.RIGHT) {
                    ClickAndDrop2.this.moveStackToStackNumberSendRequest(clickAndDropActor, clickAndDropActorDst, 1);
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

    public void moveStackToStackSendRequest(ClickAndDropActor srcActor, ClickAndDropActor dstActor) {
        moveStackToStackNumberSendRequest(srcActor, dstActor, InventoryManager.tailleStack(srcActor.getStack()));
    }

    public void moveStackToStackNumberSendRequest(ClickAndDropActor srcActor, ClickAndDropActor dstActor, int number) {
        ComponentMapper<InventoryComponent> mInventory = world.getMapper(InventoryComponent.class);
        ComponentMapper<ItemComponent> mItem = world.getMapper(ItemComponent.class);

        int srcInventoryId = systemsAdminClient.getUuidEntityManager().getEntityId(srcActor.getInventoryUuid());
        int dstInventoryId = systemsAdminClient.getUuidEntityManager().getEntityId(dstActor.getInventoryUuid());
        UUID[] srcItemsUuid = new UUID[number];

        if (srcInventoryId == -1) {
            logger.info("Inventory src not found by stack");
            return;
        }
        if (dstInventoryId == -1) {
            logger.info("Inventory dst not found by stack");
            return;
        }

        InventoryComponent srcInventoryComponent = mInventory.get(srcInventoryId);
        InventoryComponent dstInventoryComponent = mInventory.get(dstInventoryId);

        for (int i = 0; i < srcItemsUuid.length; i++) {
            srcItemsUuid[i] = context.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(srcActor.getStack()[i]);
        }
        int dstIndex = -1;
        for (int i = 0; i < dstInventoryComponent.inventory.length; i++) {
            if (dstInventoryComponent.inventory[i] == dstActor.getStack()) {
                dstIndex = i;
            }
        }

        UUID srcInventoryUuid = context.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(srcInventoryId);
        UUID dstInventoryUuid = context.getSystemsAdminClient().getUuidEntityManager().getEntityUuid(dstInventoryId);

        context.getClientConnexion().sendAndFlushPacketToServer(
                new MoveStackToStackPacket(srcInventoryUuid, dstInventoryUuid, srcItemsUuid, dstIndex)
        );
    }
}
