package ch.realmtech.game.listener;

import ch.realmtech.RealmTech;
import ch.realmtech.game.map.RealmTechTiledMap;
import ch.realmtech.input.InputMapper;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
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
                    gameMap.getLayerTiledLayer(0).setCell((int) gameCoordinate.x, (int) gameCoordinate.y, null);
                }
                if (pointerMapper.button == InputMapper.rightClick.button) {
                    TextureAtlas groundTitlesAtlas = context.getAssetManager().get("texture/atlas/ground/ground-tiles.atlas", TextureAtlas.class);
                    TextureRegion texture = groundTitlesAtlas.getRegions().get(MathUtils.random(0,groundTitlesAtlas.getRegions().size - 1));
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(texture));
                    gameMap.getLayerTiledLayer(0).setCell((int) gameCoordinate.x, (int) gameCoordinate.y, cell);
                }
            }
        }
    }
}
