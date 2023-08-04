package ch.realmtech.helper;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ButtonsMenu {
    public static TextButton textButton(RealmTech context, String text) {
        TextButton textButton = new TextButton(text, context.getSkin());
        textButton.getLabel().addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (fromActor != null) {
                    super.enter(event, x, y, pointer, fromActor);
                    context.getSoundManager().playClickOverMenu();
                }
            }
        });
        return textButton;
    }

    public static TextButton textButton(RealmTech context, String text, OnClick onClick) {
        TextButton textButton = textButton(context, text);
        textButton.getLabel().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                onClick.clicked(event, x, y);
                context.getSoundManager().playClickMenu();
            }
        });
        return textButton;
    }
}
