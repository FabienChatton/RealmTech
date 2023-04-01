package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class GameScreen extends AbstractScreen {
    private TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Box2DDebugRenderer box2DDebugRenderer;

    public GameScreen(RealmTech context) {
        super(context);
        //tiledMap = context.getAssetManager().get("map/mapTest.tmx", TiledMap.class);
        tiledMap = new TiledMap();
        creerTiledMap(tiledMap);

        mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, RealmTech.UNITE_SCALE, context.getGameStage().getBatch());
        box2DDebugRenderer = new Box2DDebugRenderer();
        context.getEcsEngine().createPlayer();

    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(context.getInputManager());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isTouched()) {
            Vector3 gameCoordinate = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            TiledMapTileLayer.Cell selectedCell = ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getCell((int) gameCoordinate.x, (int) gameCoordinate.y);
            if (selectedCell == null) {
                TextureRegion violet = new TextureRegion(new Texture(Gdx.files.internal("map/violet.png")));
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(violet));
                ((TiledMapTileLayer) tiledMap.getLayers().get(0)).setCell((int) gameCoordinate.x, (int) gameCoordinate.y, cell);
            } else {
                ((TiledMapTileLayer) tiledMap.getLayers().get(0)).setCell((int) gameCoordinate.x, (int) gameCoordinate.y, null);
            }
        }
    }

    @Override
    public void draw() {
        mapRenderer.setView((OrthographicCamera) context.getGameStage().getCamera());
        mapRenderer.render();
        ecsEngine.update(Gdx.graphics.getDeltaTime());
        uiStage.draw();
        box2DDebugRenderer.render(context.world, gameCamera.combined);
    }

    @Override
    public void dispose() {
        super.dispose();
        box2DDebugRenderer.dispose();
    }

    private void creerTiledMap(TiledMap tiledMap) {
        int width = 10;
        int height = 10;
        TextureRegion violet = new TextureRegion(new Texture(Gdx.files.internal("map/violet.png")));
        tiledMap.getLayers().add(new TiledMapTileLayer(width, height, 32, 32));
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(violet));
                    ((TiledMapTileLayer) tiledMap.getLayers().get(0)).setCell(i, j, cell);
                } else if (i % 2 == 1 && j % 2 == 1) {
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(violet));
                    ((TiledMapTileLayer) tiledMap.getLayers().get(0)).setCell(i, j, cell);
                }
            }
        }
    }
}
