package ch.realmtech.game.listener;

import ch.realmtech.RealmTech;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.level.cell.GameCellFactory;
import ch.realmtech.game.level.chunk.GameChunk;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
import com.badlogic.gdx.Gdx;
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
//                    if (context.getWorldMap() != null) {
//                        final GameCell gameCell = context.getWorldMapManager().getTopCell(worldX, worldY);
//                        if (gameCell != null && gameCell.getCellType() != null) {
//                            if (gameCell.getCellType().cellBehavior.getBreakWith() == ItemType.PELLE) {
//                                context.getWorldMap().setTopCell(worldX, worldY, null);
//                            }
//                        }
//                    } TODO remettre qu'on puisse enlever une cellule
                }
                if (pointerMapper.button == InputMapper.rightClick.button) {
//                    if (context.getWorldMap() != null) {
//                        int worldX = (int) gameCoordinate.x;
//                        int worldY = (int) gameCoordinate.y;
//                        GameChunk gameChunk = context.getWorldMap().getGameChunk(worldX, worldY);
//                        GameCell newTopCell = GameCellFactory.createByTypeOnTop(gameChunk,GameCell.getInnerChunkPossByWorldPoss(worldX, worldY), CellType.GRASS);
//                        gameChunk.setCell(newTopCell);
//                    } TODO placer cellule
                }
            }
        }
    }
}
