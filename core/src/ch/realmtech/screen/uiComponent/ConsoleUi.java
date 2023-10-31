package ch.realmtech.screen.uiComponent;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ConsoleUi {
    private final Skin skin;
    private final RealmTech context;
    private final Window consoleWindow;
    private final TextField textFieldInput;
    private final TextButton flushButton;
    private final Table outputTextContainer;
    private final ScrollPane scroll;
    private ByteArrayOutputStream baosOutput;
    private final PrintWriter printWriter;


    public ConsoleUi(Skin skin, RealmTech context) {
        this.skin = skin;
        this.context = context;
        consoleWindow = new Window("Console", skin);
        textFieldInput = new TextField("echo bonjour", skin);
        flushButton = new TextButton("send", skin);
        outputTextContainer = new Table(skin);
        baosOutput = new ByteArrayOutputStream();
        printWriter = new PrintWriter(baosOutput, true, StandardCharsets.US_ASCII) {
            @Override
            public void flush() {
                writeToConsole(baosOutput.toString());
                baosOutput = new ByteArrayOutputStream();
            }
        };
        consoleWindow.setBounds(100, 100, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 100);

        scroll = new ScrollPane(outputTextContainer, skin);
        scroll.setFadeScrollBars(false);
        consoleWindow.add(scroll).colspan(2).expand().fill().padBottom(10f).left().row();
        consoleWindow.add(textFieldInput).expandX().fillX();
        consoleWindow.add(flushButton).row();

        flushButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sendCommandeRequest();
            }
        });

        textFieldInput.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    sendCommandeRequest();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void sendCommandeRequest() {
        outputTextContainer.add(new Label("> " + textFieldInput.getText(), skin)).expandX().fillX().left().row();
        context.getEcsEngine().getCommandClientExecute().execute(textFieldInput.getText(), printWriter);
        textFieldInput.setText("");
    }

    public Window getConsoleWindow() {
        return consoleWindow;
    }

    public void writeToConsole(String s) {
        outputTextContainer.add(new Label(s, skin)).expandX().fillX().left().row();
        scroll.scrollTo(0, 0, 0, 0); // sroll vers le bas
    }
}
