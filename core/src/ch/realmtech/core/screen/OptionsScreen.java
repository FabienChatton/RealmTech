package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.helper.ButtonsMenu.ScrollPaneMenu;
import ch.realmtech.core.helper.ButtonsMenu.SliderMenu;
import ch.realmtech.core.helper.ButtonsMenu.TextButtonMenu;
import ch.realmtech.core.helper.OnClick;
import ch.realmtech.core.input.InputMapper;
import ch.realmtech.core.observer.Subcriber;
import ch.realmtech.server.options.OptionSlider;
import ch.realmtech.server.registry.Entry;
import ch.realmtech.server.registry.OptionClientEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static ch.realmtech.core.helper.ButtonsMenu.CheckBoxMenu;

public class OptionsScreen extends AbstractScreen {
    private final Table optionTable;

    public OptionsScreen(RealmTech context) {
        super(context);
        optionTable = new Table(skin);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void show() {
        super.show();

        TextButtonMenu backButton = new TextButtonMenu(context, "back", new OnClick((event, x, y) -> {
            context.setScreen(oldScreen);
            InputMapper.reset();
        }));
        TextButtonMenu resetOptionButton = new TextButtonMenu(context, "reset options", new OnClick((event, x, y) -> {
            List<? extends Entry> clientOptions = RegistryUtils.findEntries(context.getRootRegistry(), "#clientOptions");
            clientOptions.forEach((clientOption) -> ((OptionClientEntry<?>) clientOption).resetValue());

            hide();
            show();
        }));
        resetOptionButton.setDefaultColor(Color.RED);

        List<? extends Entry> clientOptions = RegistryUtils.findEntries(context.getRootRegistry(), "#clientOptions");

        clientOptions.forEach((clientOption) -> {
            OptionClientEntry<?> clientOptionEntry = (OptionClientEntry<?>) clientOption;
            optionTable.add(new Label(clientOption.getName(), skin)).left();
            Type genericSuperclass = ((ParameterizedType) clientOptionEntry.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            if (genericSuperclass.getTypeName().equals("java.lang.Integer")) {
                OptionSlider sliderAnnotation = clientOptionEntry.getClass().getDeclaredAnnotation(OptionSlider.class);
                OptionClientEntry<Integer> clientOptionEntryInt = (OptionClientEntry<Integer>) clientOption;
                if (sliderAnnotation != null) {
                    newSlider(optionTable, sliderAnnotation.min(), sliderAnnotation.max(), sliderAnnotation.stepSize(), sliderAnnotation.vertical(), skin, clientOptionEntryInt);
                } else {
                    optionTable.add(newKeysBind(clientOptionEntryInt)).padLeft(10f).padBottom(10f);
                }
            } else if (genericSuperclass.getTypeName().equals("java.lang.Boolean")) {
                OptionClientEntry<Boolean> clientOptionEntryBoolean = (OptionClientEntry<Boolean>) clientOptionEntry;
                newBooleanRun(optionTable, clientOptionEntryBoolean);
            } else if (genericSuperclass.getTypeName().equals("java.lang.String")) {
                OptionClientEntry<String> clientOptionEntryString = (OptionClientEntry<String>) clientOptionEntry;
                optionTable.row();
                optionTable.add(newTextFiledBind(clientOptionEntryString)).colspan(2).width(600f).padBottom(20f).center();
                optionTable.row();
            }
            optionTable.row();
        });

        ScrollPaneMenu scrollPane = new ScrollPaneMenu(context, optionTable);
        uiTable.add(scrollPane).width(800).center().row();
        scrollPane.focus();
        //reset button
        Table bottomButtons = new Table();
        bottomButtons.add(resetOptionButton).padRight(25f);
        bottomButtons.add(backButton).padLeft(25f);
        uiTable.add(bottomButtons);
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

    private TextButton newKeysBind(OptionClientEntry<Integer> optionClientEntry) {
        TextButtonMenu field = new TextButtonMenu(context, Input.Keys.toString(optionClientEntry.getValue()));
        field.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setInputProcessor(context.getInputManager());
                field.setText("?");
                Subcriber<Integer> nextKey = new Subcriber<>() {
                    @Override
                    public void receive(Integer keyValue) {
                        optionClientEntry.setValue(keyValue);
                        field.setText(Input.Keys.toString(keyValue));
                        context.getInputManager().keysSignal.remove(this);
                        Gdx.input.setInputProcessor(uiStage);
                    }
                };
                context.getInputManager().keysSignal.add(nextKey);
            }
        });
        return field;
    }

    private void newSlider(Table table, int min, int max, int stepSize, boolean vertical, Skin skin, OptionClientEntry<Integer> optionClientEntry) {
        SliderMenu slider = new SliderMenu(context, min, max, stepSize, vertical);
        slider.setValue(optionClientEntry.getValue());
        final Label label = new Label(String.valueOf((int) slider.getValue()), skin);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                optionClientEntry.setValue((int) slider.getValue());
                label.setText((int) slider.getValue());
            }
        });
        table.add(slider).left();
        table.add(label).padLeft(10f).padBottom(10f).row();
    }

    private Actor newTextFiledBind(OptionClientEntry<String> clientOptionEntryString) {
        TextField textField = new TextField(clientOptionEntryString.getValue(), context.getSkin());
        textField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                clientOptionEntryString.setValue(((TextField) actor).getText());
            }
        });

        return textField;
    }

    private void newBooleanRun(Table table, OptionClientEntry<Boolean> optionClientEntry) {
        CheckBoxMenu checkBox = new CheckBoxMenu(context, null);
        checkBox.setChecked(optionClientEntry.getValue());
        checkBox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                optionClientEntry.setValue(checkBox.isChecked());
            }
        });
        table.add(checkBox).padLeft(10f).padBottom(10f).row();
    }
}
