package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import ch.realmtech.input.InputMapper;
import ch.realmtech.sound.SoundManager;
import ch.realmtechServer.ecs.component.*;
import ch.realmtechServer.ecs.system.PlayerMouvementSystemServer;
import ch.realmtechServer.mod.PlayerFootStepSound;
import ch.realmtechServer.packet.serverPacket.PlayerMovePacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

@All({PlayerComponent.class,
        MovementComponent.class,
        PositionComponent.class,
        Box2dComponent.class})
public class PlayerMouvementSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    @Wire
    private InputMapper inputMapper;
    @Wire
    private SoundManager soundManager;
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
        if (!PlayerComponent.isMainPlayer(entityId, world)) {
            return;
        }
        byte inputKeys = PlayerMouvementSystemServer.getKeysInputPlayerMouvement(
                InputMapper.moveLeft.isPressed,
                InputMapper.moveDown.isPressed,
                InputMapper.moveUp.isPressed,
                InputMapper.moveRight.isPressed
        );
        if (inputKeys != 0) {
            context.getConnexionHandler().sendAndFlushPacketToServer(new PlayerMovePacket(inputKeys));
        }

    }

    private void playFootStepSound(int cellId) {
        if (cellId != -1) {
            PlayerFootStepSound playerFootStepSound = mCell.get(cellId).cellRegisterEntry.getCellBehavior().getPlayerFootStepSound();
            if (playerFootStepSound != null) {
                soundManager.playFootStep(playerFootStepSound.playerFootStepSound(), playerFootStepSound.volume());
            }
        }
    }
}
