package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
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
        optionTable.add(new Label("renderDistance", skin)).left();
        newSliderRenderDistance(optionTable); // s'ajoute lui-même dans le tableau
        optionTable.add(new Label("chunkUpdate", skin)).left();
        newSliderNumberChunkParUpdate(optionTable);

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

    private void newSliderRenderDistance(Table table) {
        final Slider slider = new Slider(RealmTechDataCtrl.Option.RENDER_DISTANCE_MIN, RealmTechDataCtrl.Option.RENDER_DISTANCE_MAX, 1, false, skin);
        slider.setValue(context.getRealmTechDataCtrl().option.renderDistance.intValue());
        final Label labelRenderDistanceNumber = new Label(String.valueOf((int) slider.getValue()), skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.getRealmTechDataCtrl().option.renderDistance.set((int) slider.getValue());
                labelRenderDistanceNumber.setText(String.format("%d", (int) slider.getValue()));
            }
        });
        table.add(slider).left();
        table.add(labelRenderDistanceNumber).padLeft(10f).padBottom(10f).row();
    }

    private void newSliderNumberChunkParUpdate(Table table) {
        final Slider slider = new Slider(1, 10, 1, false, skin);
        slider.setValue(context.getRealmTechDataCtrl().option.chunkParUpdate.intValue());
        final Label labelNumberChunkParUpdate = new Label(String.valueOf((int) slider.getValue()), skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                context.getRealmTechDataCtrl().option.chunkParUpdate.set((int) slider.getValue());
                labelNumberChunkParUpdate.setText(String.format("%d", (int) slider.getValue()));
            }
        });

        table.add(slider).left();
        table.add(labelNumberChunkParUpdate).padLeft(10f).padBottom(10f).row();
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
