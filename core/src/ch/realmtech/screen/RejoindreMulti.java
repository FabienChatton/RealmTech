package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.helper.ButtonsMenu.TextButtonMenu;
import ch.realmtech.helper.OnClick;
import ch.realmtech.helper.Popup;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import java.io.IOException;

public class RejoindreMulti extends AbstractScreen {
    public static final String TAG = RejoindreMulti.class.getSimpleName();

    public RejoindreMulti(RealmTech context) {
        super(context);
        TextField host = new TextField("localhost", context.getSkin());
        TextField port = new TextField("25533", context.getSkin());
        host.setMessageText("host");
        port.setMessageText("port");
        TextButton rejoindre = new TextButtonMenu(context, "rejoindre", new OnClick((event, x, y) -> {
            try {
                rejoindre(host.getText(), Integer.parseInt(port.getText()));
            } catch (NumberFormatException e) {
                Popup.popupErreur(context, e.getMessage(), context.getUiStage());
            }
        }));
        TextButtonMenu backButton = new TextButtonMenu(context, "back", new OnClick((event, x, y) -> context.setScreen(ScreenType.MENU)));
        uiTable.add(host).width(300).pad(10f);
        uiTable.add(port).width(100).pad(10f);
        uiTable.add(rejoindre).padLeft(10).row();
        uiTable.add(backButton).colspan(2);
    }

    public void rejoindre(String host, int port) {
        try {
            context.rejoindreMulti(host, port);
        } catch (Exception e) {
            Gdx.app.error(TAG, e.getMessage(), e);
            Popup.popupErreur(context, e.getMessage(), uiStage);
        }

    }
}
