package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.ItemBarManager;
import ch.realmtech.game.mod.RealmTechCoreItem;
import ch.realmtech.game.mod.RealmTechCoreMod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

import java.io.IOException;

public class GameScreen extends AbstractScreen {
    private final static String TAG = GameScreen.class.getSimpleName();

    private final Box2DDebugRenderer box2DDebugRenderer;

    public GameScreen(RealmTech context) throws IOException {
        super(context);
        box2DDebugRenderer = new Box2DDebugRenderer();
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
            if (MathUtils.randomBoolean()) {
                context.getEcsEngine().getItemManager().newItemOnGround(worldPosition.x, worldPosition.y, RealmTechCoreMod.REALM_TECH_CORE_ITEM_REGISTRY.get(RealmTechCoreItem.PELLE_ITEM));
            } else {
                context.getEcsEngine().getItemManager().newItemOnGround(worldPosition.x, worldPosition.y, RealmTechCoreMod.REALM_TECH_CORE_ITEM_REGISTRY.get(RealmTechCoreItem.PIOCHE_ITEM));
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) context.getEcsEngine().togglePlayerInventoryWindow();

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) context.getEcsEngine().getSystem(ItemBarManager.class).setSlotSelected((byte) 0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) context.getEcsEngine().getSystem(ItemBarManager.class).setSlotSelected((byte) 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) context.getEcsEngine().getSystem(ItemBarManager.class).setSlotSelected((byte) 2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) context.getEcsEngine().getSystem(ItemBarManager.class).setSlotSelected((byte) 3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) context.getEcsEngine().getSystem(ItemBarManager.class).setSlotSelected((byte) 4);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) context.getEcsEngine().getSystem(ItemBarManager.class).setSlotSelected((byte) 5);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) context.getEcsEngine().getSystem(ItemBarManager.class).setSlotSelected((byte) 6);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) context.getEcsEngine().getSystem(ItemBarManager.class).setSlotSelected((byte) 7);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)) context.getEcsEngine().getSystem(ItemBarManager.class).setSlotSelected((byte) 8);
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP)) context.getEcsEngine().getSystem(ItemBarManager.class).slotSelectedUp();
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN)) context.getEcsEngine().getSystem(ItemBarManager.class).slotSelectedDown();
    }

    @Override
    public void draw() {
        ecsEngine.process(Gdx.graphics.getDeltaTime());
        //box2DDebugRenderer.render(context.physicWorld, gameCamera.combined);
        uiStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        box2DDebugRenderer.dispose();
    }
}
