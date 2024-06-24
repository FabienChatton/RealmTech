package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.input.InputMapper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GamePauseScreen extends AbstractScreen {
    public GamePauseScreen(RealmTech context) {
        super(context);
        uiTable.add(new Label("Pause",skin)).center();
        uiTable.row();
        TextButton resumeButton = new TextButton("Resume",skin);
        resumeButton.addListener(resumeGame());
        uiTable.add(resumeButton);
        uiTable.row();
        TextButton optionButton = new TextButton("Options", skin);
        optionButton.addListener(openOption());
        uiTable.add(optionButton);
        uiTable.row();
        TextButton quiteAndSave = new TextButton("Sauvegarder et quitter", skin);
        quiteAndSave.addListener(quiteAndSave());
        uiTable.add(quiteAndSave);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.postRunnable(() -> context.setScreen(ScreenType.GAME_SCREEN));
        }
    }

    @Override
    public void draw() {
        context.drawGameScreen();
        super.draw();
    }

    @Override
    public void show() {
        super.show();
        InputMapper.reset();
        context.getEcsEngine().getSystemDisableOnPlayerInventoryOpen().onInventoryOpen(context.getEcsEngine().getWorld());
    }

    @Override
    public void hide() {
        super.hide();
        if (context.getEcsEngine() != null)
            context.getEcsEngine().getSystemDisableOnPlayerInventoryOpen().onInventoryClose(context.getEcsEngine().getWorld());
    }

    private ClickListener quiteAndSave() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.closeEcs();
                context.setScreen(ScreenType.MENU);
            }
        };
    }

    private ClickListener openOption() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.OPTION);
            }
        };
    }

    private ClickListener resumeGame(){
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.GAME_SCREEN);
            }
        };
    }
}
