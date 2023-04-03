package ch.realmtech.game.map;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.component.CellComponent;
import ch.realmtech.game.level.cell.CellType;
import ch.realmtech.game.worldGeneration.PerlinNoise;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.ashley.core.Entity;

import java.util.Random;

public class RealmTechTiledMap extends TiledMap {
    private final RealmTech context;
    private final Array<Entity> gameCells;
    public RealmTechTiledMap(RealmTech context) {
        this.context = context;
        gameCells = new Array<>();
    }

    public void creerMapAleatoire() {
        if (!gameCells.isEmpty()) {
            for (Entity gameCell : gameCells) {
                context.getEcsEngine().removeEntity(gameCell);
            }
        }
        int width = 100;
        int height = 100;
        PerlinNoise perlinNoise = new PerlinNoise(new Random(), 1, width, height);
        perlinNoise.initialise();
        getProperties().put("spawn-point", new Vector2(width / 2, height/2));
        context.getEcsEngine().generateBodyWorldBorder(0,0,width,width);
        getLayers().add(new TiledMapTileLayer(width, height, 32, 32));

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                final CellType cellType;
                if (perlinNoise.getGrid()[i][j] > 0f && perlinNoise.getGrid()[i][j] < 0.5f){
                    cellType = CellType.GRASS;
                } else if (perlinNoise.getGrid()[i][j] >= 0.5f) {
                    cellType = CellType.SAND;
                } else {
                    cellType = CellType.WATER;
                }
                Entity entityCell = context.getEcsEngine().createCell(cellType);
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(entityCell.getComponent(CellComponent.class).cell.getTile());
                gameCells.add(entityCell);
                getLayerTiledLayer(0).setCell(i, j, cell);
            }
        }
    }

    public TiledMapTileLayer getLayerTiledLayer(int index) {
        return (TiledMapTileLayer) super.getLayers().get(index);
    }

    public TiledMapTileLayer getLayerTiledLayer(String name) {
        return (TiledMapTileLayer) super.getLayers().get(name);
    }
}
