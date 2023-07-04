package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
import ch.realmtech.options.BooleanRun;
import ch.realmtech.options.IntegerRun;
import ch.realmtech.options.RealmTechDataCtrl;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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

        // keyMoveForward
        optionTable.add(new Label("keyMoveForward", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.keyMoveForward)).padLeft(10f).padBottom(10f).row();
        // keyMoveLeft
        optionTable.add(new Label("keyMoveLeft", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.keyMoveLeft)).padLeft(10f).padBottom(10f).row();
        // keyMoveRight
        optionTable.add(new Label("keyMoveRight", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.keyMoveRight)).padLeft(10f).padBottom(10f).row();
        // keyMoveBack
        optionTable.add(new Label("keyMoveBack", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.keyMoveBack)).padLeft(10f).padBottom(10f).row();
        // openInventory
        optionTable.add(new Label("openInventory", skin)).left();
        optionTable.add(newKeysBind(context.getRealmTechDataCtrl().option.openInventory)).padLeft(10f).padBottom(10f).row();
        // renderDistance
        optionTable.add(new Label("renderDistance", skin)).left();
        newSlider(optionTable, RealmTechDataCtrl.Option.RENDER_DISTANCE_MIN, RealmTechDataCtrl.Option.RENDER_DISTANCE_MAX, 1, false, skin, context.getRealmTechDataCtrl().option.renderDistance);
        // chunkUpdate
        optionTable.add(new Label("chunkUpdate", skin)).left();
        newSlider(optionTable, 1, 10, 1, false, skin, context.getRealmTechDataCtrl().option.chunkParUpdate);
        // full screen
        optionTable.add(new Label("fullScreen", skin)).left();
        newBooleanRun(optionTable, context.getRealmTechDataCtrl().option.fullScreen);
        // vsync
        optionTable.add(new Label("vsync", skin)).left();
        newBooleanRun(optionTable, context.getRealmTechDataCtrl().option.vsync);
        // fps
        optionTable.add(new Label("fps", skin)).left();
        newSlider(optionTable, 0, 300, 5, false, skin, context.getRealmTechDataCtrl().option.fps);


        //reset button
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
    private void newSlider(Table table, int min, int max, int stepSize, boolean vertical, Skin skin, AtomicInteger atomicIntegerOption) {
        final Slider slider = new Slider(min, max, stepSize, vertical, skin);
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
        final Slider slider = new Slider(min, max, stepSize, vertical, skin);
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

    private void newBooleanRun(Table table, BooleanRun booleanRun) {
        CheckBox checkBox = new CheckBox(null, skin);
        checkBox.setChecked(booleanRun.get());
        checkBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                booleanRun.set(checkBox.isChecked());
            }
        });
        table.add(checkBox).padLeft(10f).padBottom(10f).row();
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
