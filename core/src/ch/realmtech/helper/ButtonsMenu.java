package ch.realmtech.helper;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ButtonsMenu {
    public static class TextButtonMenu extends TextButton {
        private final static Color DEFAULT_COLOR = Color.LIGHT_GRAY;
        private Color buttonColor = DEFAULT_COLOR;

        public void setDefaultColor(Color color) {
            buttonColor = color;
            setColor(color);
        }

        public TextButtonMenu(RealmTech context, String text) {
            super(text, context.getSkin());
            getLabel().addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (!event.isStopped()) {
                        super.enter(event, x, y, pointer, fromActor);
                        context.getSoundManager().playClickOverMenu();
                    }
                }
            });

            getLabel().addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    getLabel().setColor(Color.WHITE);
                }
            });

            getLabel().addListener(new ClickListener() {
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    applyDefaultColor();
                }
            });

            applyDefaultColor();
        }

        private void applyDefaultColor() {
            getLabel().setColor(Color.LIGHT_GRAY);
        }

        public TextButtonMenu(RealmTech context, String text, OnClick onClick) {
            this(context, text);
            getLabel().addCaptureListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    onClick.clicked(event, x, y);
                    context.getSoundManager().playClickMenu();
                    event.stop();
                }
            });
        }
    }

    public static class SliderMenu extends Slider {
        public SliderMenu(RealmTech context, float min, float max, float stepSize, boolean vertical) {
            super(min, max, stepSize, vertical, context.getSkin());
            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    context.getSoundManager().playClickMenu();
                }
            });
        }

        public SliderMenu(RealmTech context, float min, float max, float stepSize, boolean vertical, ChangeListener changeListener) {
            this(context, min, max, stepSize, vertical);
            addListener(changeListener);
        }
    }

    public static class CheckBoxMenu extends CheckBox {

        public CheckBoxMenu(RealmTech context, String text) {
            super(text, context.getSkin());
            addListener(new OnClick((event, x, y) -> {
                context.getSoundManager().playClickMenu();
            }));
        }
    }
}
