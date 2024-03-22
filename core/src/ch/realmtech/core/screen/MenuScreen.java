package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.helper.ButtonsMenu.TextButtonMenu;
import ch.realmtech.core.helper.OnClick;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.server.ServerContext;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuScreen extends AbstractScreen {
    public MenuScreen(RealmTech context) {
        super(context);
        TextButton singlePlayer = new TextButtonMenu(context, "Single player", new OnClick((event, x, y) -> context.setScreen(ScreenType.SELECTION_DE_SAUVEGARDE)));
        TextButton multiPlayer = new TextButtonMenu(context, "Multi Player", new OnClick((event, x, y) -> context.setScreen(ScreenType.REJOINDRE_MULTI)));
        TextButton options = new TextButtonMenu(context, "Options", new OnClick((event, x, y) -> context.setScreen(ScreenType.OPTION)));
        TextButton quit = new TextButtonMenu(context, "Quit", new OnClick(
                (event, x, y) -> Popup.popupConfirmation(context, "Do you really want to leave me :(", uiStage, () -> Gdx.app.exit())
        ));
        TextButton reLogin = new TextButtonMenu(context, "re login", new OnClick(((event, x, y) -> context.setScreen(ScreenType.AUTHENTICATE))));
        uiTable.add(createAnimatedLogo()).padBottom(10f).row();
        uiTable.add(singlePlayer).width(250).padBottom(10f).row();
        uiTable.add(multiPlayer).width(250).padBottom(10f).row();
        uiTable.add(options).width(250).padBottom(10f).row();
        uiTable.add(reLogin).width(250).padBottom(10f).row();
        uiTable.add(quit).width(250).padBottom(10f).row();
        uiTable.add(new Label("Version : " + ServerContext.REALMTECH_VERSION, context.getSkin())).center();
    }

    private Image createAnimatedLogo() {
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
