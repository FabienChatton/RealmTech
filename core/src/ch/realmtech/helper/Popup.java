package ch.realmtech.helper;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public final class Popup {
    private static final GlyphLayout glyphLayout;

    static {
        glyphLayout = new GlyphLayout();
    }

    public static void popupErreur(RealmTech context, String message, Stage stage) {
        String erreur = "Erreur";
        float width = Math.min(getWidht(context, message), Gdx.graphics.getWidth() - 20);
        Label label = new Label(message, context.getSkin());
        label.setWrap(true);
        Dialog popupErreur = new Dialog(erreur, context.getSkin());
        popupErreur.getContentTable().add(label).width(width);
        popupErreur.button(ButtonsMenu.textButton(context, "ok"));
        popupErreur.key(Input.Keys.ESCAPE, true);
        popupErreur.setWidth(width);
        popupErreur.setHeight(150 + (message.length()));
        popupErreur.setResizable(true);
        popupErreur.setPosition(stage.getViewport().getScreenWidth() / 2f - popupErreur.getWidth() / 2f, stage.getViewport().getScreenHeight() / 2f - popupErreur.getHeight() / 2f);
        stage.addActor(popupErreur);
    }

    public static void popupConfirmation(RealmTech context, String message, Stage stage, Runnable okRunnable) {
        String confirmation = "confirmation";
        float width = Math.min(getWidht(context, message), Gdx.graphics.getWidth() - 20);
        Label label = new Label(message, context.getSkin());
        label.setWrap(true);
        Dialog popupConfirmation = new Dialog(confirmation, context.getSkin());
        popupConfirmation.getContentTable().add(label).width(width);
        TextButton okButton = ButtonsMenu.textButton(context, "oui", new OnClick((event, x, y) -> okRunnable.run()));
        popupConfirmation.button(okButton);
        TextButton nonButton = ButtonsMenu.textButton(context, "non");
        nonButton.setColor(Color.RED);
        popupConfirmation.button(nonButton);
        popupConfirmation.setWidth(width);
        popupConfirmation.setHeight(150 + (message.length()));
        popupConfirmation.setResizable(true);
        popupConfirmation.setPosition(stage.getViewport().getScreenWidth() / 2f - popupConfirmation.getWidth() / 2f, stage.getViewport().getScreenHeight() / 2f - popupConfirmation.getHeight() / 2f);
        stage.addActor(popupConfirmation);
    }

    private static float getWidht(RealmTech context, String message) {
        glyphLayout.setText(context.getSkin().getFont("helvetica"), message);
        return glyphLayout.width + 10;
    }
}
