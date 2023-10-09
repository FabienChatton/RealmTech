package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.game.ecs.system.ItemBarManager;
import ch.realmtech.game.ecs.system.PlayerInventorySystem;
import ch.realmtech.helper.Popup;
import ch.realmtechServer.ecs.system.MapManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GameScreen extends AbstractScreen {
    private final static Logger logger = LoggerFactory.getLogger(GameScreen.class);

    private final Box2DDebugRenderer box2DDebugRenderer;
    private final Table debugTable;
    private final Label fpsLabel;
    private final Label gameCoo;
    private final Label chunkPos;
    private final Label innerChunk;

    public GameScreen(RealmTech context) throws IOException {
        super(context);
        box2DDebugRenderer = new Box2DDebugRenderer();
        debugTable = new Table(skin);
        fpsLabel = new Label("", skin);
        gameCoo = new Label("", skin);
        chunkPos = new Label("", skin);
        innerChunk = new Label("", skin);

        debugTable.add(fpsLabel).left().row();
        debugTable.add(gameCoo).left().row();
        debugTable.add(chunkPos).left().row();
        debugTable.add(innerChunk).left().row();
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
            context.getSystem(PlayerInventorySystem.class).closePlayerInventory();
            context.setScreen(ScreenType.GAME_PAUSE);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            if (uiTable.getChildren().contains(debugTable, true)) {
                uiTable.clear();
                uiStage.setDebugAll(false);
            } else {
                uiTable.add(debugTable).expand().left().top();
                uiStage.setDebugAll(true);
            }
        }
        // open inventory
        if (Gdx.input.isKeyJustPressed(context.getDataCtrl().option.openInventory.get())) {
            context.getEcsEngine().togglePlayerInventoryWindow();
        }

        if (Gdx.input.isKeyJustPressed(context.getDataCtrl().option.keyDropItem.get())) {
            context.getEcsEngine().dropCurentPlayerItem();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1))
            context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).setSlotSelected((byte) 0);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2))
            context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).setSlotSelected((byte) 1);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3))
            context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).setSlotSelected((byte) 2);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4))
            context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).setSlotSelected((byte) 3);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5))
            context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).setSlotSelected((byte) 4);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6))
            context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).setSlotSelected((byte) 5);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7))
            context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).setSlotSelected((byte) 6);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8))
            context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).setSlotSelected((byte) 7);
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_9))
            context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).setSlotSelected((byte) 8);
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_UP)) context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).slotSelectedUp();
        if (Gdx.input.isKeyJustPressed(Input.Keys.PAGE_DOWN)) context.getEcsEngine().getWorld().getSystem(ItemBarManager.class).slotSelectedDown();

        fpsLabel.setText(String.format("FPS : %d", Gdx.graphics.getFramesPerSecond()));
        gameCoo.setText(String.format("Game X : %f\nGame Y : %f", gameCamera.position.x, gameCamera.position.y));
        chunkPos.setText(String.format("Chunk Pos X : %d\nChunk Pos Y : %d", MapManager.getChunkPos(gameCamera.position.x), MapManager.getChunkPos(gameCamera.position.y)));
        innerChunk.setText(String.format("Inner X : %d\nInner Y : %d", MapManager.getInnerChunk(gameCamera.position.x), MapManager.getInnerChunk(gameCamera.position.y)));
    }

    @Override
    public void draw() {
        try {
            context.process(Gdx.graphics.getDeltaTime());
            uiStage.draw();
            if (uiStage.isDebugAll()) {
                box2DDebugRenderer.render(context.getEcsEngine().physicWorld, gameCamera.combined);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            context.setScreen(ScreenType.MENU);
            Popup.popupErreur(context, e.getMessage(), context.getUiStage());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        box2DDebugRenderer.dispose();
    }
}
