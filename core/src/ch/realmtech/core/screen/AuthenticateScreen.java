package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.helper.ButtonsMenu;
import ch.realmtech.core.helper.OnClick;
import ch.realmtech.core.helper.Popup;
import ch.realmtech.core.option.PersisteCredential;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticateScreen extends AbstractScreen {
    private final static Logger logger = LoggerFactory.getLogger(AuthenticateScreen.class);
    private final TextField usernameTextField;
    private final TextField passwordTextField;
    private final ButtonsMenu.TextButtonMenu loginButton;
    private final ButtonsMenu.TextButtonMenu anonymousButton;
    private final OnClick loginAction;
    private final OnClick anonymousAction;
    private final CheckBox saveCredentialCheckBox;
    public AuthenticateScreen(RealmTech context) {
        super(context);
        usernameTextField = new TextField(null, skin);
        passwordTextField = new TextField(null, skin);
        usernameTextField.setMessageText("username");
        passwordTextField.setMessageText("password");
        passwordTextField.setPasswordMode(true);
        loginAction = new OnClick((event, x, y)  -> verifyCredential());
        anonymousAction = new OnClick((event, x, y) -> context.setScreen(ScreenType.MENU));
        loginButton = new ButtonsMenu.TextButtonMenu(context, "login", loginAction);
        anonymousButton = new ButtonsMenu.TextButtonMenu(context, "continue as anonymous", anonymousAction);
        saveCredentialCheckBox = new CheckBox("save credential", skin);

        uiTable.add(new Label("Login", skin)).pad(50f);
        uiTable.row();
        uiTable.add(usernameTextField).width(200f);
        uiTable.row();
        uiTable.add(passwordTextField).width(200f).padBottom(50f);
        uiTable.row();
        uiTable.add(loginButton);
        uiTable.add(saveCredentialCheckBox);
        uiTable.row();
        uiTable.add(anonymousButton);

        PersisteCredential.loadCredential().ifPresent(credential -> {
            String username = credential[0];
            String password = credential[1];
            try {
                context.getAuthControllerClient().verifyLogin(username, password);
                Gdx.app.postRunnable(() -> context.setScreen(ScreenType.MENU));
            } catch (Exception e) {
                logger.error("Saved credential are not valide");
            }
        });
    }

    private void verifyCredential() {
        try {
            String username = usernameTextField.getText();
            String password = passwordTextField.getText();
            context.getAuthControllerClient().verifyLogin(username, password);
            if (saveCredentialCheckBox.isChecked()) {
                PersisteCredential.persisteCredential(username, password);
            }
            context.setScreen(ScreenType.MENU);
        } catch (Exception e) {
            Popup.popupErreur(context, e.getMessage(), context.getUiStage());
        }
    }

}