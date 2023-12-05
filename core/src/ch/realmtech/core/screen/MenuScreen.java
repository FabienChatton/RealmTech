package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.helper.ButtonsMenu.TextButtonMenu;
import ch.realmtech.core.helper.OnClick;
import ch.realmtech.core.helper.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuScreen extends AbstractScreen {
    public MenuScreen(RealmTech context) {
        super(context);
        TextButton selectionnerSauvegarde = new TextButtonMenu(context, "Single player", new OnClick((event, x, y) -> context.setScreen(ScreenType.SELECTION_DE_SAUVEGARDE)));
        TextButton rejoindreMulti = new TextButtonMenu(context, "Multi Player", new OnClick((event, x, y) -> context.setScreen(ScreenType.REJOINDRE_MULTI)));
        TextButton option = new TextButtonMenu(context, "Options", new OnClick((event, x, y) -> context.setScreen(ScreenType.OPTION)));
        TextButton quitter = new TextButtonMenu(context, "Quit", new OnClick(
                (event, x, y) -> Popup.popupConfirmation(context, "Voulez-vous vraiment quitter ? :(", uiStage, () -> Gdx.app.exit())
        ));
        uiTable.add(creerLogo()).padBottom(10f).row();
        uiTable.add(selectionnerSauvegarde).width(250).padBottom(10f).row();
        uiTable.add(rejoindreMulti).width(250).padBottom(10f).row();
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