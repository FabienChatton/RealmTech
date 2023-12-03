package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.helper.ButtonsMenu;
import ch.realmtech.core.helper.OnClick;
import ch.realmtech.core.helper.Popup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class AuthenticateScreen extends AbstractScreen {
    private final TextField usernameTextField;
    private final TextField passwordTextField;
    private final ButtonsMenu.TextButtonMenu loginButton;
    private final ButtonsMenu.TextButtonMenu anonymousButton;
    private final OnClick loginAction;
    private final OnClick anonymousAction;
    public AuthenticateScreen(RealmTech context) {
        super(context);
        usernameTextField = new TextField(null, skin);
        passwordTextField = new TextField(null, skin);
        usernameTextField.setMessageText("username");
        passwordTextField.setMessageText("password");
        passwordTextField.setPasswordMode(true);
        loginAction = new OnClick((event, x, y)  -> {
            try {
                context.getAuthControllerClient().verifyLogin(usernameTextField.getText(), passwordTextField.getText());
                context.setScreen(ScreenType.MENU);
            } catch (Exception e) {
                Popup.popupErreur(context, e.getMessage(), context.getUiStage());
            }
        });
        anonymousAction = new OnClick((event, x, y) -> context.setScreen(ScreenType.MENU));
        loginButton = new ButtonsMenu.TextButtonMenu(context, "login", loginAction);
        anonymousButton = new ButtonsMenu.TextButtonMenu(context, "continue as anonymous", anonymousAction);

        uiTable.add(new Label("Login", skin)).pad(50f);
        uiTable.row();
        uiTable.add(usernameTextField).width(200f);
        uiTable.row();
        uiTable.add(passwordTextField).width(200f).padBottom(50f);
        uiTable.row();
        uiTable.add(loginButton);
        uiTable.row();
        uiTable.add(anonymousButton);
    }

}
