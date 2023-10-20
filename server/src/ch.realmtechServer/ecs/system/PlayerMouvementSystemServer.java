package ch.realmtechServer.ecs.system;

import ch.realmtechServer.ecs.component.*;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.managers.TagManager;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;
@All(PlayerConnexionComponent.class)
public class PlayerMouvementSystemServer extends IteratingSystem {
    private ComponentMapper<MovementComponent> mMovement;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<InfCellComponent> mCell;
    private final static float FORCE = 5f;
    @Override
    protected void process(int entityId) {
        MovementComponent movementComponent = mMovement.get(entityId);
        Box2dComponent box2dComponent = mBox2d.get(entityId);
        float impulseX = (movementComponent.speed.x - box2dComponent.body.getLinearVelocity().x) * box2dComponent.body.getMass();
        float impulseY = (movementComponent.speed.y - box2dComponent.body.getLinearVelocity().y) * box2dComponent.body.getMass();
        Vector2 worldCenter = box2dComponent.body.getWorldCenter();
        box2dComponent.body.applyLinearImpulse(
                impulseX,
                impulseY,
                worldCenter.x,
                worldCenter.y,
                true
        );
        movementComponent.speed.x = 0;
        movementComponent.speed.y = 0;
    }

    public void playerMove(Channel clientChannel, byte inputKeys) {
        int playerId = world.getSystem(PlayerManagerServer.class).getPlayerByChannel(clientChannel);
        MovementComponent movementComponent = mMovement.get(playerId);
        Box2dComponent box2dComponent = mBox2d.get(playerId);
        PositionComponent positionComponent = mPos.get(playerId);
        InfMapComponent infMapComponent = world.getSystem(TagManager.class).getEntity("infMap").getComponent(InfMapComponent.class);

        int chunkId = world.getSystem(MapManager.class).getChunk(infMapComponent.infChunks, positionComponent.x, positionComponent.y);
        byte innerChunkX = MapManager.getInnerChunk(positionComponent.x);
        byte innerChunkY = MapManager.getInnerChunk(positionComponent.y);
        int topCellId = world.getSystem(MapManager.class).getTopCell(chunkId, innerChunkX, innerChunkY);
        InfCellComponent infCellComponent = mCell.get(topCellId);
        float cellSpeedEffect = infCellComponent.cellRegisterEntry.getCellBehavior().getSpeedEffect();

        float acceleration = FORCE * cellSpeedEffect;

        float xFactor = 0;
        float yFactor = 0;
        if (isInputKeysLeft(inputKeys)) {
            xFactor += -acceleration;
        }
        if (isInputKeysDown(inputKeys)) {
            yFactor += -acceleration;
        }
        if (isInputKeysUp(inputKeys)) {
            yFactor += acceleration;
        }
        if (isInputKeysRight(inputKeys)) {
            xFactor += acceleration;
        }

        movementComponent.speed.x = xFactor;
        movementComponent.speed.y = yFactor;
    }

    public static byte getKeysInputPlayerMouvement(boolean left, boolean down, boolean up, boolean right) {
        byte inputKeys = 0;
        if (left) inputKeys |= 1 << 3;
        if (down) inputKeys |= 1 << 2;
        if (up)   inputKeys |= 1 << 1;
        if (right)inputKeys |= 1 << 0;
        return inputKeys;
    }

    public static boolean isInputKeysLeft(int inputKeys) {
        return (inputKeys >> 3 & 1) == 1;
    }

    public static boolean isInputKeysDown(int inputKeys) {
        return (inputKeys >> 2 & 1) == 1;
    }

    public static boolean isInputKeysUp(int inputKeys) {
        return (inputKeys >> 1 & 1) == 1;
    }

    public static boolean isInputKeysRight(int inputKeys) {
        return (inputKeys >> 0 & 1) == 1;
    }
}
