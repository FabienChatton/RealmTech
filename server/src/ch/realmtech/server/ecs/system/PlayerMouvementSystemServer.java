package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import io.netty.channel.Channel;
@All({PlayerConnexionComponent.class, PlayerMovementComponent.class})
@Exclude(PlayerDeadComponent.class)
public class PlayerMouvementSystemServer extends IteratingSystem {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<PlayerMovementComponent> mMovement;
    private ComponentMapper<Box2dComponent> mBox2d;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<CellComponent> mCell;
    private final static float FORCE = 8f;
    @Override
    protected void process(int entityId) {
        PlayerMovementComponent playerMovementComponent = mMovement.get(entityId);
        Box2dComponent box2dComponent = mBox2d.get(entityId);
        float impulseX = (playerMovementComponent.speed.x - box2dComponent.body.getLinearVelocity().x) * box2dComponent.body.getMass();
        float impulseY = (playerMovementComponent.speed.y - box2dComponent.body.getLinearVelocity().y) * box2dComponent.body.getMass();
        Vector2 worldCenter = box2dComponent.body.getWorldCenter();
        box2dComponent.body.applyLinearImpulse(
                impulseX,
                impulseY,
                worldCenter.x,
                worldCenter.y,
                true
        );
        playerMovementComponent.speed.x = 0;
        playerMovementComponent.speed.y = 0;
    }

    public void playerMove(Channel clientChannel, byte inputKeys) {
        int playerId = systemsAdminServer.getPlayerManagerServer().getPlayerByChannel(clientChannel);
        PlayerMovementComponent playerMovementComponent = mMovement.get(playerId);
        Box2dComponent box2dComponent = mBox2d.get(playerId);
        PositionComponent positionComponent = mPos.get(playerId);
        InfMapComponent infMapComponent = systemsAdminServer.getTagManager().getEntity("infMap").getComponent(InfMapComponent.class);
        int worldPosX = MapManager.getWorldPos(positionComponent.x);
        int worldPosY = MapManager.getWorldPos(positionComponent.y);

        int chunkId = systemsAdminServer.getMapManager().getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infMapComponent.infChunks);
        byte innerChunkX = MapManager.getInnerChunk(worldPosX);
        byte innerChunkY = MapManager.getInnerChunk(worldPosY);
        int topCellId = systemsAdminServer.getMapManager().getTopCell(chunkId, innerChunkX, innerChunkY);
        if (topCellId == -1) return;
        CellComponent cellComponent = mCell.get(topCellId);
        float cellSpeedEffect = cellComponent.cellRegisterEntry.getCellBehavior().getSpeedEffect();

        float acceleration = FORCE * cellSpeedEffect;

        Vector2 vector2 = new Vector2();
        if (isInputKeysLeft(inputKeys)) {
            vector2.x += -1;
        }
        if (isInputKeysDown(inputKeys)) {
            vector2.y += -1;
        }
        if (isInputKeysUp(inputKeys)) {
            vector2.y += 1;
        }
        if (isInputKeysRight(inputKeys)) {
            vector2.x += 1;
        }

        vector2.nor();

        playerMovementComponent.speed.x = vector2.x * acceleration;
        playerMovementComponent.speed.y = vector2.y * acceleration;
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
