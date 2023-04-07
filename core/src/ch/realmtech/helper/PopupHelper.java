package ch.realmtech.helper;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

public final class PopupHelper implements HelperSetContext {

    private static final GlyphLayout glyphLayout;
    static RealmTech context;

    static {
        glyphLayout = new GlyphLayout();
    }

    public static Dialog popupErreur(String message) {
        String erreur = "Erreur";
        Dialog popupErreur = new Dialog(erreur, context.getSkin()).text(message).button("ok");

        popupErreur.setWidth(getWidht(message));
        popupErreur.setPosition(context.getUiStage().getViewport().getScreenWidth() / 2f - popupErreur.getWidth() /2f, context.getUiStage().getViewport().getScreenHeight() / 2f - popupErreur.getHeight() / 2f);
        return popupErreur;
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
