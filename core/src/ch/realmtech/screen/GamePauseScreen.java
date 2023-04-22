package ch.realmtech.screen;

import ch.realmtech.RealmTech;
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
        TextButton quiteAndSave = new TextButton("Sauvegarder et quitter",skin);
        quiteAndSave.addListener(quiteAndSave());
        uiTable.add(quiteAndSave);
    }

    @Override
    public void draw() {
        context.drawGameScreen();
        super.draw();
    }

    private ClickListener quiteAndSave() {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.quiteAndSave();
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
