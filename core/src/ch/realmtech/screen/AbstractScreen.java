package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class AbstractScreen implements Screen {
    protected final RealmTech context;
    protected final Viewport gameViewport;
    protected final Viewport uiViewport;
    protected final OrthographicCamera gameCamera;
    protected final Table uiTable;
    protected final Stage gameStage;
    protected final Stage uiStage;
    protected final OrthographicCamera uiCamera;
    protected final Skin skin;
    protected ScreenType oldScreen;

    public AbstractScreen(RealmTech context) {
        this.context = context;
        this.skin = this.context.getSkin();
        this.gameStage = context.getGameStage();
        this.uiStage = context.getUiStage();
        this.gameViewport = gameStage.getViewport();
        this.gameCamera = (OrthographicCamera) this.gameViewport.getCamera();
        this.uiViewport = uiStage.getViewport();
        this.uiCamera = (OrthographicCamera) this.uiViewport.getCamera();

        this.uiTable = new Table();
        this.uiTable.setFillParent(true);
    }

    @Override
    public void show() {
        uiStage.addActor(uiTable);
        Gdx.input.setInputProcessor(uiStage);
    }

    @Override
    public final void render(float delta) {
        update(delta);
        clearScreen();
        draw();
    }

    public void update(final float delta) {
        gameStage.act(delta);
        uiStage.act(delta);
    }

    public void clearScreen(){
        Gdx.gl.glClearColor(0.2f, 0, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void draw() {
        gameStage.draw();
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
        uiViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        uiStage.clear();
    }

    @Override
    public void dispose() {

    }

    public final void setOldScreen(ScreenType oldScreen) {
        this.oldScreen = oldScreen;
    }
}
