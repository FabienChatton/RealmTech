package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.healper.PopupHealper;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class Menu extends AbstractScreen {
    public Menu(RealmTech context) {
        super(context);
        uiTable.add(new Label("Bienvenu dans realmTech", skin));
    }

    @Override
    public void show() {
        super.show();
        uiStage.addActor(PopupHealper.popupErreur("Bienvenu"));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
