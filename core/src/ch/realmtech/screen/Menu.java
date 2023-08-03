package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.helper.OnClick;
import ch.realmtech.helper.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Menu extends AbstractScreen {
    public Menu(RealmTech context) {
        super(context);
        TextButton selectionnerSauvegarde = new TextButton("Jouer", skin);
        selectionnerSauvegarde.addListener(lancerLeJeu());
        TextButton option = new TextButton("Options", skin);
        TextButton quitter = new TextButton("Quitter", skin);
        quitter.addListener(quitter());
        option.addListener(openOption());
        uiTable.add(creerLogo()).padBottom(10f).row();
        uiTable.add(selectionnerSauvegarde).width(250).padBottom(10f).row();
        uiTable.add(option).width(250).padBottom(10f).row();
        uiTable.add(quitter).width(250).padBottom(10f).row();
    }

    private OnClick quitter() {
        return new OnClick((event, x, y) -> Popup.popupConfirmation(context, "Voulez-vous vraiment quitter ? :(", uiStage, () -> Gdx.app.exit()));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    private ClickListener lancerLeJeu() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(ScreenType.SELECTION_DE_SAUVEGARDE);
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
