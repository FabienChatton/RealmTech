package ch.realmtech.game.listener;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.CellComponent;
import ch.realmtech.game.ecs.system.CellManager;
import ch.realmtech.game.ecs.system.ChunkManager;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
import com.artemis.ComponentMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class GameWorldInputListener implements Subcriber<InputMapper.PointerMapper> {
    private final RealmTech context;
    public GameWorldInputListener(RealmTech context) {
        this.context = context;
    }

    @Override
    public void receive(InputMapper.PointerMapper pointerMapper) {
        // supprime ou place cellule sur la carte
        if (pointerMapper.isPressed) {
            Vector3 gameCoordinate = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (pointerMapper.pointer == 0) {
                if (pointerMapper.button == InputMapper.leftClick.button) {
                    int worldX = (int) gameCoordinate.x;
                    int worldY = (int) gameCoordinate.y;
                    ComponentMapper<CellComponent> cMap = context.getEcsEngine().getEcsWorld().getMapper(CellComponent.class);
                    int topCellId = context.getEcsEngine().getSystem(CellManager.class).getTopCell(worldX, worldY);
                    if (topCellId != -1) {
                        CellComponent cellComponent = cMap.create(topCellId);
                        if (cellComponent.cellType.cellBehavior.getBreakWith() == ItemType.PELLE) {
                            context.getEcsEngine().getEcsWorld().delete(topCellId);
                        }
                    }
                }
                if (pointerMapper.button == InputMapper.rightClick.button) {
                    int worldX = (int) gameCoordinate.x;
                    int worldY = (int) gameCoordinate.y;
                    ComponentMapper<CellComponent> cMap = context.getEcsEngine().getEcsWorld().getMapper(CellComponent.class);
                    int parentChunkId = context.getEcsEngine().getSystem(ChunkManager.class).getChunk(worldX, worldY);
                    CellManager cellManager = context.getEcsEngine().getSystem(CellManager.class);
                    byte innerChunkX = cellManager.getInnerChunkX(worldX);
                    byte innerChunkY = cellManager.getInnerChunkY(worldY);
                    CellType cellType = CellType.getCellTypeByID((byte) MathUtils.random(1, CellType.values().length - 1));
                    byte onTopLayer = cellManager.getOnTopLayer(worldX, worldY);
                    if (onTopLayer != -1) {
                        cellManager.newCell(parentChunkId, innerChunkX, innerChunkY, onTopLayer, cellType);
                    }
                }
            }
        }
    }
}
