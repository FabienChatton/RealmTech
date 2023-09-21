package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.helper.ButtonsMenu.ScrollPaneMenu;
import ch.realmtech.helper.ButtonsMenu.SliderMenu;
import ch.realmtech.helper.ButtonsMenu.TextButtonMenu;
import ch.realmtech.helper.OnClick;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
import ch.realmtechServer.options.BooleanRun;
import ch.realmtechServer.options.IntegerRun;
import ch.realmtechServer.options.DataCtrl;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.realmtech.helper.ButtonsMenu.CheckBoxMenu;

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

        TextButtonMenu backButton = new TextButtonMenu(context, "back", new OnClick((event, x, y) -> {
            context.setScreen(oldScreen);
            InputMapper.reset();
        }));
        TextButtonMenu resetOptionButton = new TextButtonMenu(context, "reset options", new OnClick((event, x, y) -> {
            context.getDataCtrl().option.setDefaultOption();
            hide();
            show();
        }));
        resetOptionButton.setDefaultColor(Color.RED);

        // keyMoveForward
        optionTable.add(new Label("keyMoveForward", skin)).left();
        optionTable.add(newKeysBind(context.getDataCtrl().option.keyMoveForward)).padLeft(10f).padBottom(10f).row();
        // keyMoveLeft
        optionTable.add(new Label("keyMoveLeft", skin)).left();
        optionTable.add(newKeysBind(context.getDataCtrl().option.keyMoveLeft)).padLeft(10f).padBottom(10f).row();
        // keyMoveRight
        optionTable.add(new Label("keyMoveRight", skin)).left();
        optionTable.add(newKeysBind(context.getDataCtrl().option.keyMoveRight)).padLeft(10f).padBottom(10f).row();
        // keyMoveBack
        optionTable.add(new Label("keyMoveBack", skin)).left();
        optionTable.add(newKeysBind(context.getDataCtrl().option.keyMoveBack)).padLeft(10f).padBottom(10f).row();
        // keyDropItem
        optionTable.add(new Label("keyDropItem", skin)).left();
        optionTable.add(newKeysBind(context.getDataCtrl().option.keyDropItem)).padLeft(10f).padBottom(10f).row();
        // openInventory
        optionTable.add(new Label("openInventory", skin)).left();
        optionTable.add(newKeysBind(context.getDataCtrl().option.openInventory)).padLeft(10f).padBottom(10f).row();
        // renderDistance
        optionTable.add(new Label("renderDistance", skin)).left();
        newSlider(optionTable, DataCtrl.Option.RENDER_DISTANCE_MIN, DataCtrl.Option.RENDER_DISTANCE_MAX, 1, false, skin, context.getDataCtrl().option.renderDistance);
        // chunkUpdate
        optionTable.add(new Label("chunkUpdate", skin)).left();
        newSlider(optionTable, 1, 10, 1, false, skin, context.getDataCtrl().option.chunkParUpdate);
        // full screen
        optionTable.add(new Label("fullScreen", skin)).left();
        newBooleanRun(optionTable, context.getDataCtrl().option.fullScreen);
        // vsync
        optionTable.add(new Label("vsync", skin)).left();
        newBooleanRun(optionTable, context.getDataCtrl().option.vsync);
        // fps
        optionTable.add(new Label("fps", skin)).left();
        newSlider(optionTable, 0, 300, 5, false, skin, context.getDataCtrl().option.fps);
        optionTable.add(new Label("inventoryBlur", skin)).left();
        newBoolean(optionTable, context.getDataCtrl().option.inventoryBlur);
        // sound
        optionTable.add(new Label("volume sonore", skin)).left();
        newSlider(optionTable, 0, 100, 5, false, skin, context.getDataCtrl().option.sound);

        //reset button
        optionTable.add(resetOptionButton).left();
        optionTable.add(backButton).right();

        ScrollPaneMenu scrollPane = new ScrollPaneMenu(context, optionTable);
        uiTable.add(scrollPane).expand().fill().center();
        scrollPane.focus();
    }

    @Override
    public void hide() {
        super.hide();
        uiTable.clear();
        optionTable.clear();
    }

    @Override
    public void resume() {
        super.resume();
    }

    private TextButton newKeysBind(AtomicInteger atomicInteger) {
        TextButtonMenu field = new TextButtonMenu(context, Input.Keys.toString(atomicInteger.get()));
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
    private void newSlider(Table table, int min, int max, int stepSize, boolean vertical, Skin skin, AtomicInteger atomicIntegerOption) {
        SliderMenu slider = new SliderMenu(context, min, max, stepSize, vertical);
        slider.setValue(atomicIntegerOption.get());
        final Label label = new Label(String.valueOf((int) slider.getValue()), skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                atomicIntegerOption.set((int) slider.getValue());
                label.setText((int) slider.getValue());
            }
        });
        table.add(slider).left();
        table.add(label).padLeft(10f).padBottom(10f).row();
    }

    private void newSlider(Table table, int min, int max, int stepSize, boolean vertical, Skin skin, IntegerRun integerRun) {
        SliderMenu slider = new SliderMenu(context, min, max, stepSize, vertical);
        slider.setValue(integerRun.get());
        final Label label = new Label(String.valueOf((int) slider.getValue()), skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                integerRun.set((int) slider.getValue());
                label.setText((int) slider.getValue());
            }
        });
        table.add(slider).left();
        table.add(label).padLeft(10f).padBottom(10f).row();
    }

    private void newBoolean(Table table, AtomicBoolean atomicBoolean) {
        CheckBoxMenu checkBox = new CheckBoxMenu(context, null);
        checkBox.setChecked(atomicBoolean.get());
        checkBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                atomicBoolean.set(checkBox.isChecked());
            }
        });
        table.add(checkBox).padLeft(10f).padBottom(10f).row();
    }

    private void newBooleanRun(Table table, BooleanRun booleanRun) {
        CheckBoxMenu checkBox = new CheckBoxMenu(context, null);
        checkBox.setChecked(booleanRun.get());
        checkBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                booleanRun.set(checkBox.isChecked());
            }
        });
        table.add(checkBox).padLeft(10f).padBottom(10f).row();
    }
}
