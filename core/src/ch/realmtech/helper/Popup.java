package ch.realmtech.helper;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public final class Popup implements SetContext {
    private static final GlyphLayout glyphLayout;
    static RealmTech context;

    static {
        glyphLayout = new GlyphLayout();
    }

    public static void popupErreur(String message, Stage stage) {
        String erreur = "Erreur";
        float width = Math.min(getWidht(message), Gdx.graphics.getWidth() - 20);
        Label label = new Label(message, context.getSkin());
        label.setWrap(true);
        Dialog popupErreur = new Dialog(erreur, context.getSkin());
        popupErreur.getContentTable().add(label).width(width);
        popupErreur.button("ok");
        popupErreur.key(Input.Keys.ESCAPE, true);
        popupErreur.setWidth(width);
        popupErreur.setHeight(150 + (message.length()));
        popupErreur.setResizable(true);
        popupErreur.setPosition(stage.getViewport().getScreenWidth() / 2f - popupErreur.getWidth() / 2f, stage.getViewport().getScreenHeight() / 2f - popupErreur.getHeight() / 2f);
        stage.addActor(popupErreur);
    }

    private static float getHeight(String message) {
        glyphLayout.setText(context.getSkin().getFont("helvetica"), message);
        return glyphLayout.height + 10;
    }

    private static float getWidht(String message) {
        glyphLayout.setText(context.getSkin().getFont("helvetica"), message);
        return glyphLayout.width + 10;
    }
}
