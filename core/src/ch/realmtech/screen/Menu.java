package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.healper.PopupHealper;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Menu extends AbstractScreen {
    public Menu(RealmTech context) {
        super(context);
        TextButton lancerLeJeu = new TextButton("Lancer le jeu",skin);
        lancerLeJeu.addListener(lancerLeJeu());
        uiTable.add(new Label("Bienvenu dans realmTech", skin));
        uiTable.add(lancerLeJeu);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    private ClickListener lancerLeJeu() {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.GAME_SCREEN);
            }
        };
    }

}
