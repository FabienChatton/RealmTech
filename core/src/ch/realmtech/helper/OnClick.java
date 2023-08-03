package ch.realmtech.helper;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class OnClick extends ClickListener {
    public OnClick(OnClickFunction onClickFunction) {
        this.onClickFunction = onClickFunction;
    }

    private final OnClickFunction onClickFunction;

    public interface OnClickFunction {
        void click(InputEvent event, float x, float y);

    }

    public void clicked(InputEvent event, float x, float y) {
        onClickFunction.click(event, x, y);
    }
}
