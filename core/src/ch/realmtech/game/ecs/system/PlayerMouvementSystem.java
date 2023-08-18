package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.*;
import ch.realmtech.game.level.cell.Cells;
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
        Box2dComponent.class})
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
    private final static float FORCE = 1f;

    @Override
    protected void process(int entityId) {
        PlayerComponent playerComponent = mPlayer.get(entityId);
        MovementComponent movementComponent = mMouvement.get(entityId);
        PositionComponent positionComponent = mPosition.get(entityId);
        Box2dComponent box2dComponent = mBox2d.get(entityId);
        int cellId = context.getEcsEngine().getCell(positionComponent.x, positionComponent.y, Cells.Layer.GROUND.layer);
        xFactor = 0;
        yFactor = 0;
        if (cellId != -1) {
            InfCellComponent infCellComponent = mCell.get(cellId);
            if (context.getInputManager().isKeyPressed(InputMapper.moveForward.key.get())) {
                if (!playerComponent.moveUp) {
                    playerComponent.cooldown = 0;
                }
                playerComponent.moveUp = true;
                directionChange = true;
                yFactor = FORCE;
                yFactor *= infCellComponent.cellRegisterEntry.getCellBehavior().getSpeedEffect();
            } else {
                playerComponent.moveUp = false;
            }
            if (context.getInputManager().isKeyPressed(InputMapper.moveLeft.key.get())) {
                if (!playerComponent.moveLeft) {
                    playerComponent.cooldown = 0;
                }
                playerComponent.moveLeft = true;
                directionChange = true;
                xFactor = -FORCE;
                xFactor *= infCellComponent.cellRegisterEntry.getCellBehavior().getSpeedEffect();
            } else {
                playerComponent.moveLeft = false;
            }
            if (context.getInputManager().isKeyPressed(InputMapper.moveBack.key.get())) {
                if (!playerComponent.moveDown) {
                    playerComponent.cooldown = 0;
                }
                playerComponent.moveDown = true;
                directionChange = true;
                yFactor = -FORCE;
                yFactor *= infCellComponent.cellRegisterEntry.getCellBehavior().getSpeedEffect();
            } else {
                playerComponent.moveDown = false;
            }
            if (context.getInputManager().isKeyPressed(InputMapper.moveRight.key.get())) {
                if (!playerComponent.moveRight) {
                    playerComponent.cooldown = 0;
                }
                playerComponent.moveRight = true;
                directionChange = true;
                xFactor = FORCE;
                xFactor *= infCellComponent.cellRegisterEntry.getCellBehavior().getSpeedEffect();
            } else {
                playerComponent.moveRight = false;
            }
            if (xFactor == 0 && yFactor == 0) {
                playerComponent.cooldown = 0;
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
