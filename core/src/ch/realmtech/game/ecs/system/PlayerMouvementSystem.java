package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.mod.PlayerFootStepSound;
import ch.realmtech.input.InputMapper;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

@All({PlayerComponent.class,
        MovementComponent.class,
        PositionComponent.class,
        Box2dComponent.class,
        TextureComponent.class})
public class PlayerMouvementSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<InfCellComponent> mCell;
    private ComponentMapper<MovementComponent> mMouvement;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<Box2dComponent> mBox2d;
    private boolean directionChange = false;
    private float xFactor = 0;
    private float yFactor = 0;

    @Override
    protected void process(int entityId) {
        PlayerComponent playerComponent = mPlayer.create(entityId);
        MovementComponent movementComponent = mMouvement.create(entityId);
        PositionComponent positionComponent = mPosition.create(entityId);
        Box2dComponent box2dComponent = mBox2d.create(entityId);
        //int cellId = context.getEcsEngine().getCellManager().getCell((int) positionComponent.x, (int) positionComponent.y, (byte) 0);
        int cellId = context.getEcsEngine().getCell(positionComponent.x, positionComponent.y);
        xFactor = 0;
        yFactor = 0;
        if (context.getInputManager().isKeyPressed(InputMapper.moveForward.key)) {
            directionChange = true;
            yFactor = 1;
            if (cellId != -1) {
                yFactor *= mCell.create(cellId).cellRegisterEntry.getCellBehavior().getSpeedEffect();
            }
        }
        if (context.getInputManager().isKeyPressed(InputMapper.moveLeft.key)) {
            directionChange = true;
            xFactor = -1;
            if (cellId != -1) {
                xFactor *= mCell.create(cellId).cellRegisterEntry.getCellBehavior().getSpeedEffect();
            }
        }
        if (context.getInputManager().isKeyPressed(InputMapper.moveBack.key)) {
            directionChange = true;
            yFactor = -1;
            if (cellId != -1) {
                yFactor *= mCell.create(cellId).cellRegisterEntry.getCellBehavior().getSpeedEffect();
            }
        }
        if (context.getInputManager().isKeyPressed(InputMapper.moveRight.key)) {
            directionChange = true;
            xFactor = 1;
            if (cellId != -1) {
                xFactor *= mCell.create(cellId).cellRegisterEntry.getCellBehavior().getSpeedEffect();
            }
        }

        ComponentMapper<ItemComponent> mInventory = world.getMapper(ItemComponent.class);
        int[][] inventory = world.getSystem(InventoryManager.class).getInventory(entityId);
        for (int i = 0; i < inventory.length; i++) {
            for (int j = 0; j < inventory[i].length; j++) {
                ItemComponent itemComponent = mInventory.get(inventory[i][j]);
                if (itemComponent != null) {
                    float speedEffect = itemComponent.itemRegisterEntry.getItemBehavior().getSpeedEffect();
                    xFactor *= speedEffect;
                    yFactor *= speedEffect;
                }
            }
        }
        if (directionChange) {
            directionChange = false;
            movementComponent.speed.x = xFactor * movementComponent.speedMeterParSeconde;
            movementComponent.speed.y = yFactor * movementComponent.speedMeterParSeconde;
            playFootStepSound(cellId);
        } else {
            movementComponent.speed.x = 0;
            movementComponent.speed.y = 0;
            xFactor = 0;
            yFactor = 0;
        }

        final Vector2 worldCenter = box2dComponent.body.getWorldCenter();
        box2dComponent.body.applyLinearImpulse(
                (movementComponent.speed.x - box2dComponent.body.getLinearVelocity().x) * box2dComponent.body.getMass(),
                (movementComponent.speed.y - box2dComponent.body.getLinearVelocity().y) * box2dComponent.body.getMass(),
                worldCenter.x,
                worldCenter.y,
                true
        );
    }

    private void playFootStepSound(int cellId) {
        if (cellId != -1) {
            PlayerFootStepSound playerFootStepSound = mCell.get(cellId).cellRegisterEntry.getCellBehavior().getPlayerFootStepSound();
            if (playerFootStepSound != null) {
                context.getEcsEngine().playFootStep(playerFootStepSound);
            }
        }
    }
}
