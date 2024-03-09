package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.input.InputMapper;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.system.PlayerMouvementSystemServer;
import ch.realmtech.server.packet.serverPacket.PlayerMovePacket;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

@All({PlayerComponent.class,
        PlayerMovementComponent.class,
        PositionComponent.class,
        Box2dComponent.class})
public class PlayerMouvementSystem extends IteratingSystem {
    @Wire(name = "context")
    private RealmTech context;
    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<PlayerMovementComponent> mMouvement;
    private ComponentMapper<PositionComponent> mPosition;
    private ComponentMapper<Box2dComponent> mBox2d;
    private boolean directionChange = false;
    private float xFactor = 0;
    private float yFactor = 0;

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
            context.getClientConnexion().sendAndFlushPacketToServer(new PlayerMovePacket(inputKeys));
            int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;

            PositionComponent positionComponent = mPosition.get(entityId);

        }

    }
}
