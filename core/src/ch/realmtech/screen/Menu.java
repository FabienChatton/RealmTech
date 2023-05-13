package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Menu extends AbstractScreen {
    public Menu(RealmTech context) {
        super(context);
        TextButton selectionnerSauvegarde = new TextButton("Sélectionner une sauvegarde",skin);
        selectionnerSauvegarde.addListener(lancerLeJeu());
        uiTable.add(new Label("Bienvenue dans realmTech", skin));
        uiTable.row();
        uiTable.add(selectionnerSauvegarde);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    private ClickListener lancerLeJeu() {
        return new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.SELECTION_DE_SAUVEGARDE);
            }
        };
    }

}
