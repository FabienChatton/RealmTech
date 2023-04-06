package ch.realmtech.game.listener;

import ch.realmtech.RealmTech;
import ch.realmtech.game.item.ItemType;
import ch.realmtech.game.level.cell.GameCell;
import ch.realmtech.game.level.map.RealmTechTiledMap;
import ch.realmtech.input.InputMapper;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class GameWorldInputListener implements Listener<InputMapper.PointerMapper> {
    private final RealmTech context;
    private final RealmTechTiledMap gameMap;
    public GameWorldInputListener(RealmTech context) {
        this.context = context;
        this.gameMap = context.gameMap;
    }

    @Override
    public void receive(Signal<InputMapper.PointerMapper> signal, InputMapper.PointerMapper pointerMapper) {
        // supprime ou place cellule sur la carte
        if (pointerMapper.isPressed) {
            Vector3 gameCoordinate = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (pointerMapper.pointer == 0) {
                if (pointerMapper.button == InputMapper.leftClick.button) {
                    int worldX = (int) gameCoordinate.x;
                    int worldY = (int) gameCoordinate.y;
                    final GameCell gameCell = gameMap.getGameCell(worldX,worldY);
                    if (gameCell != null) {
                        if (gameCell.getCellType().cellBehavior.getBreakWith() == ItemType.PELLE) {
                            gameMap.setCell(worldX, worldY, null);
                        }
                    }
                    //gameMap.getLayerTiledLayer(0).setCell((int) gameCoordinate.x, (int) gameCoordinate.y, null);
                }
                if (pointerMapper.button == InputMapper.rightClick.button) {

                    /*
                    TextureAtlas groundTitlesAtlas = context.getAssetManager().get("texture/atlas/texture.atlas", TextureAtlas.class);
                    TextureRegion texture = groundTitlesAtlas.getRegions().get(MathUtils.random(0,groundTitlesAtlas.getRegions().size - 1));
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(texture));
                    gameMap.getLayerTiledLayer(0).setCell((int) gameCoordinate.x, (int) gameCoordinate.y, cell);
                     */
                }
            }
        }
    }
}
