package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.game.mod.RealmTechCoreItem;
import ch.realmtech.game.mod.RealmTechCoreMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.io.IOException;

public class GameScreen extends AbstractScreen {

    private final Box2DDebugRenderer box2DDebugRenderer;

    public GameScreen(RealmTech context) throws IOException {
        super(context);
        box2DDebugRenderer = new Box2DDebugRenderer();
        context.getEcsEngine().spawnPlayer(context.getWorldMap().getProperties().get("spawn-point", Vector2.class));
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(context.getInputManager());
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            context.setScreen(ScreenType.GAME_PAUSE);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            try {
                context.newSaveInitWorld("test");
                context.generateNewWorld();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) {
            context.getEcsEngine().spawnPlayer(context.getWorldMap().getProperties().get("spawn-point", Vector2.class));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            Vector3 worldPosition = context.getGameStage().getCamera().unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            context.getEcsEngine().getItemManager().newItem(worldPosition.x, worldPosition.y, RealmTechCoreMod.REALM_TECH_CORE_ITEM_REGISTRY.get(RealmTechCoreItem.PELLE_ITEM));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            context.getEcsEngine().togglePlayerInventoryWindow();
        }
    }

    @Override
    public void draw() {
        ecsEngine.process(Gdx.graphics.getDeltaTime());
        box2DDebugRenderer.render(context.physicWorld, gameCamera.combined);
        uiStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        box2DDebugRenderer.dispose();
    }
}
