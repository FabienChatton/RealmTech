package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.helper.ButtonsMenu.TextButtonMenu;
import ch.realmtech.helper.OnClick;
import ch.realmtech.helper.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Menu extends AbstractScreen {
    public Menu(RealmTech context) {
        super(context);
        TextButton selectionnerSauvegarde = new TextButtonMenu(context, "Joueur", new OnClick((event, x, y) -> context.setScreen(ScreenType.SELECTION_DE_SAUVEGARDE)));
        TextButton option = new TextButtonMenu(context, "options", new OnClick((event, x, y) -> context.setScreen(ScreenType.OPTION)));
        TextButton quitter = new TextButtonMenu(context, "quitter", new OnClick(
                (event, x, y) -> Popup.popupConfirmation(context, "Voulez-vous vraiment quitter ? :(", uiStage, () -> Gdx.app.exit())
        ));
        uiTable.add(creerLogo()).padBottom(10f).row();
        uiTable.add(selectionnerSauvegarde).width(250).padBottom(10f).row();
        uiTable.add(option).width(250).padBottom(10f).row();
        uiTable.add(quitter).width(250).padBottom(10f).row();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    private Image creerLogo() {
        Image logo = new Image(context.getTextureAtlas().findRegion("logo-RealmTech"));
        float amount = 10;
        float temps = 0.5f;
        Interpolation interpolation = Interpolation.swing;
        Action repeatAction = Actions.repeat(-1,
                Actions.sequence(
                        Actions.parallel(Actions.sizeTo(logo.getWidth() - amount, logo.getHeight() - amount, temps, interpolation), Actions.moveBy(amount / 2f, amount / 2f, temps, interpolation)),
                        Actions.parallel(Actions.sizeTo(logo.getWidth(), logo.getHeight(), temps, interpolation), Actions.moveBy(-amount / 2f, -amount / 2f, temps, interpolation))
                ));
        logo.addAction(repeatAction);
        return logo;
    }
}
