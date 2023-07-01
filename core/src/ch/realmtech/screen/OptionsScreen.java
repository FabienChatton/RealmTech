package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import ch.realmtech.input.InputMapper;
import ch.realmtech.observer.Subcriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.concurrent.atomic.AtomicInteger;

public class OptionsScreen extends AbstractScreen {
    public OptionsScreen(RealmTech context) {
        super(context);

        Table optionTable = new Table(skin);
        optionTable.setFillParent(true);

        TextButton backButton = new TextButton("back", skin);
        backButton.addListener(back());

        optionTable.add(newKeysBind("keyMoveForward", context.realmTechDataCtrl.option.keyMoveForward)).row();
        optionTable.add(newKeysBind("keyMoveLeft", context.realmTechDataCtrl.option.keyMoveLeft)).row();
        optionTable.add(newKeysBind("keyMoveRight", context.realmTechDataCtrl.option.keyMoveRight)).row();
        optionTable.add(newKeysBind("keyMoveBack", context.realmTechDataCtrl.option.keyMoveBack)).row();
        optionTable.add(backButton);

        ScrollPane scrollPane = new ScrollPane(optionTable, skin);
        uiTable.add(scrollPane);
    }

    private HorizontalGroup newKeysBind(CharSequence leadingText, AtomicInteger atomicInteger) {
        final HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.addActor(new Label(leadingText, skin));
        final TextButton field = new TextButton(Input.Keys.toString(atomicInteger.get()), skin);
        horizontalGroup.addActor(field);
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
        return horizontalGroup;
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
}
