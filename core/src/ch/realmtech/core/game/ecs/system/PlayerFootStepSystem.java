package ch.realmtech.core.game.ecs.system;

import ch.realmtech.core.game.ecs.plugin.SystemsAdminClient;
import ch.realmtech.server.ecs.component.CellComponent;
import ch.realmtech.server.ecs.component.MouvementComponent;
import ch.realmtech.server.ecs.component.PlayerComponent;
import ch.realmtech.server.ecs.component.PositionComponent;
import ch.realmtech.server.ecs.system.MapManager;
import ch.realmtech.server.mod.PlayerFootStepSound;
import ch.realmtech.server.sound.SoundManager;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

@All({PlayerComponent.class, PositionComponent.class})
public class PlayerFootStepSystem extends IteratingSystem {

    @Wire
    private SystemsAdminClient systemsAdminClient;
    @Wire
    private SoundManager soundManager;

    private ComponentMapper<PlayerComponent> mPlayer;
    private ComponentMapper<PositionComponent> mPos;
    private ComponentMapper<CellComponent> mCell;
    private ComponentMapper<MouvementComponent> mMouvement;
    @Override
    protected void process(int entityId) {
        MouvementComponent playerMouvementComponent = mMouvement.get(entityId);
        if (playerMouvementComponent.oldPoss.isEmpty()) {
            return;
        }

        PositionComponent positionComponent = mPos.get(entityId);

        int mainPlayer = systemsAdminClient.getPlayerManagerClient().getMainPlayer();
        PositionComponent mainPlayerPositionComponent = mPos.get(mainPlayer);

        if (playerMouvementComponent.oldPoss.get(0).x != positionComponent.x || playerMouvementComponent.oldPoss.get(0).y != positionComponent.y) {

            int worldPosX = MapManager.getWorldPos(positionComponent.x);
            int worldPosY = MapManager.getWorldPos(positionComponent.y);

            int chunk = systemsAdminClient.getMapManager().getChunk(MapManager.getChunkPos(worldPosX), MapManager.getChunkPos(worldPosY), systemsAdminClient.getMapManager().getInfMap().infChunks);
            if (chunk == -1) return;
            int cellId = systemsAdminClient.getMapManager().getTopCell(chunk, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY));

            CellComponent cellComponent = mCell.get(cellId);
            byte layer = cellComponent.cellRegisterEntry.getCellBehavior().getLayer();

            PlayerFootStepSound playerFootStepSound = cellComponent.cellRegisterEntry.getCellBehavior().getPlayerFootStepSound();

            float dst = Vector2.dst(positionComponent.x, positionComponent.y, mainPlayerPositionComponent.x, mainPlayerPositionComponent.y);
            // limite de 15 meter sound foot step
            float volume = (-dst * 100f) / 15f + 100;

            if (volume > 0) {
                do {
                    if (playerFootStepSound != null) {
                        soundManager.playFootStep(entityId, world, playerFootStepSound, volume);
                        return;
                    }
                    byte layer1 = --layer;
                    int cell = systemsAdminClient.getMapManager().getCell(chunk, MapManager.getInnerChunk(worldPosX), MapManager.getInnerChunk(worldPosY), layer1);
                    if (cell == -1) {
                        continue;
                    }
                    playerFootStepSound = mCell.get(cell).cellRegisterEntry.getCellBehavior().getPlayerFootStepSound();
                } while (layer >= 0);
            }

        }
    }
}
