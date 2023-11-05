package ch.realmtech.screen.uiComponent;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsoleUi {
    private final Skin skin;
    private final RealmTech context;
    private final Window consoleWindow;
    private final TextField textFieldInput;
    private final TextButton flushButton;
    private final Table outputTextContainer;
    private final ScrollPane scroll;
    private final StringWriter stringWriter;
    private final PrintWriter printWriter;
    private final List<String> commandHistory;
    private final AtomicInteger commandHistoryIndex;


    public ConsoleUi(Skin skin, RealmTech context) {
        this.skin = skin;
        this.context = context;
        consoleWindow = new Window("Console", skin);
        textFieldInput = new TextField("", skin);
        flushButton = new TextButton("send", skin);
        outputTextContainer = new Table(skin);
        stringWriter = new StringWriter();
        commandHistory = new ArrayList<>();
        commandHistory.add(textFieldInput.getText());
        commandHistoryIndex = new AtomicInteger(0);
        printWriter = new PrintWriter(stringWriter) {
            @Override
            public void flush() {
                writeToConsole(stringWriter.toString());
                stringWriter.getBuffer().setLength(0);
            }
        };
        consoleWindow.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
                } else if (keycode == Input.Keys.UP) {
                    return upHistoryCommand();
                } else if (keycode == Input.Keys.DOWN) {
                    return downHistoryCommand();
                } else {
                    return false;
                }
            }

            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if (Input.Keys.valueOf(Character.toString(character)) == Input.Keys.GRAVE || character == '§') {
                    textFieldInput.setText(textFieldInput.getText().substring(0, textFieldInput.getText().length() - 1));
                    return true;
                } else if (character == '\n') {
                    return false;
                } else if (commandHistoryIndex.get() == commandHistory.size() - 1) {
                    commandHistory.set(commandHistoryIndex.get(), textFieldInput.getText());
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void sendCommandeRequest() {
        outputTextContainer.add(new Label("> " + textFieldInput.getText(), skin)).expandX().fillX().left().row();
        if (commandHistoryIndex.get() != commandHistory.size() - 1) {
            commandHistory.set(commandHistory.size() - 1, textFieldInput.getText());
            commandHistory.add("");
            commandHistoryIndex.set(commandHistory.size() - 1);
        } else {
            commandHistory.add(textFieldInput.getText());
            commandHistoryIndex.incrementAndGet();
        }

        context.getEcsEngine().getCommandClientExecute().execute(textFieldInput.getText(), printWriter);
        textFieldInput.setText("");
    }

    private boolean upHistoryCommand() {
        if (commandHistoryIndex.get() > 0) {
            commandHistoryIndex.decrementAndGet();
            textFieldInput.setText(commandHistory.get(commandHistoryIndex.get()));
            textFieldInput.setCursorPosition(textFieldInput.getText().length());
            return true;
        } else {
            return false;
        }
    }

    private boolean downHistoryCommand() {
        if (commandHistoryIndex.get() < commandHistory.size() - 1) {
            commandHistoryIndex.incrementAndGet();
            textFieldInput.setText(commandHistory.get(commandHistoryIndex.get()));
            textFieldInput.setCursorPosition(textFieldInput.getText().length());
            return true;
        }
        return false;
    }

    public Window getConsoleWindow() {
        return consoleWindow;
    }

    public void writeToConsole(String s) {
        outputTextContainer.add(new Label(s, skin)).expandX().fillX().left().row();
        scroll.scrollTo(0, 0, 0, 0); // sroll vers le bas
    }
}
