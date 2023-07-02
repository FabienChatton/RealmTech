package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.concurrent.atomic.AtomicInteger;

public class OptionsScreen extends AbstractScreen {
    private final static String TAG = OptionsScreen.class.getSimpleName();
    private final Table optionTable;

    public OptionsScreen(RealmTech context) {
        super(context);
        optionTable = new Table(skin);
    }

    @Override
    public void show() {
        super.show();
        optionTable.setFillParent(true);

        final TextButton backButton = new TextButton("back", skin);
        backButton.addListener(back());
        final TextButton resetOptionButton = new TextButton("reset options", skin);
        resetOptionButton.addListener(resetOption());
        resetOptionButton.setColor(Color.RED);

        optionTable.add(new Label("keyMoveForward", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.keyMoveForward)).padLeft(10f).padBottom(10f).row();
        optionTable.add(new Label("keyMoveLeft", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.keyMoveLeft)).padLeft(10f).padBottom(10f).row();
        optionTable.add(new Label("keyMoveRight", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.keyMoveRight)).padLeft(10f).padBottom(10f).row();
        optionTable.add(new Label("keyMoveBack", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.keyMoveBack)).padLeft(10f).padBottom(10f).row();
        optionTable.add(new Label("openInventory", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.openInventory)).padLeft(10f).padBottom(10f).row();

        optionTable.add(resetOptionButton).left();
        optionTable.add(backButton).right();

        ScrollPane scrollPane = new ScrollPane(optionTable, skin);
        uiTable.add(scrollPane);
    }

    @Override
    public void hide() {
        super.hide();
        optionTable.clear();
    }

    private TextButton newKeysBind(AtomicInteger atomicInteger) {
        final TextButton field = new TextButton(Input.Keys.toString(atomicInteger.get()), skin);
        field.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(context.getInputManager());
                field.setText("?");
                Subcriber<Integer> nextKey = new Subcriber<>() {
                    @Override
                    public void receive(Integer key) {
                        atomicInteger.set(key);
                        field.setText(Input.Keys.toString(key));
                        context.getInputManager().keysSignal.remove(this);
                        Gdx.input.setInputProcessor(uiStage);
                    }
                };
                context.getInputManager().keysSignal.add(nextKey);
            }
        });
        return field;
    }

    private ClickListener back() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.setScreen(oldScreen);
                InputMapper.reset();
            }
        };
    }

    private ClickListener resetOption() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getRealmTechDataCtrl().option.setDefaultOption();
                hide();
                show();
            }
        };
    }
}
