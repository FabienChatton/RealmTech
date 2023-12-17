package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.input.InputMapper;
import ch.realmtech.server.ecs.component.*;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.ecs.system.PlayerMouvementSystemServer;
import ch.realmtech.server.mod.PlayerFootStepSound;
import ch.realmtech.server.packet.serverPacket.PlayerMovePacket;
import ch.realmtech.server.sound.SoundManager;
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
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<MovementComponent> mMouvement;
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
            context.getConnexionHandler().sendAndFlushPacketToServer(new PlayerMovePacket(inputKeys));
            int[] infChunks = context.getEcsEngine().getMapEntity().getComponent(InfMapComponent.class).infChunks;

            PositionComponent positionComponent = mPosition.get(entityId);
            int worldPosX = MapManager.getWorldPos(positionComponent.x);
            int worldPosY = MapManager.getWorldPos(positionComponent.y);

            int chunk = context.getSystem(MapManager.class).getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), infChunks);
            int cellId = context.getSystem(MapManager.class).getTopCell(chunk, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));

            CellComponent cellComponent = mCell.get(cellId);
            byte layer = cellComponent.cellRegisterEntry.getCellBehavior().getLayer();

            PlayerFootStepSound playerFootStepSound = cellComponent.cellRegisterEntry.getCellBehavior().getPlayerFootStepSound();
            do {
                if (playerFootStepSound != null) {
                    soundManager.playFootStep(playerFootStepSound.playerFootStepSound(), playerFootStepSound.volume());
                    return;
                }
                byte layer1 = --layer;
                int cell = context.getSystem(MapManager.class).getCell(chunk, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY), layer1);
                if (cell == -1) {
                    continue;
                }
                playerFootStepSound = mCell.get(cell).cellRegisterEntry.getCellBehavior().getPlayerFootStepSound();
            } while (layer >= 0);
        }

    }
}
