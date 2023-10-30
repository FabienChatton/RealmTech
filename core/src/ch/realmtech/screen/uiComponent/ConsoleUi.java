package ch.realmtech.screen.uiComponent;

import ch.realmtech.RealmTech;
import ch.realmtech.helper.ButtonsMenu;
import ch.realmtechServer.packet.serverPacket.ConsoleCommandeRequestPacket;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ConsoleUi {
    private final Window consoleWindow;
    private final TextField textFieldInput;
    private final TextArea textAreaOutput;
    private final TextButton flushButton;

    public ConsoleUi(Skin skin, RealmTech context) {
        consoleWindow = new Window("Console", skin);
        textFieldInput = new TextField("bonjour", skin);
        textAreaOutput = new TextArea("", skin);
        flushButton = new TextButton("send", skin);
        consoleWindow.setBounds(100, 1000, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 100);
        textAreaOutput.setDisabled(true);

        ScrollPane scrollPane = new ScrollPane(textAreaOutput, skin);
        consoleWindow.add(scrollPane).expand().fill().padBottom(10f).row();
        consoleWindow.add(textFieldInput).expandX().fillX();
        consoleWindow.add(flushButton).row();

        flushButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getConnexionHandler().sendAndFlushPacketToServer(new ConsoleCommandeRequestPacket(textFieldInput.getText()));
            }
        });
    }

    public Window getConsoleWindow() {
        return consoleWindow;
    }

    public void writeToConsole(String s) {
        textAreaOutput.appendText(s);
    }
}
