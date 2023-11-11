package ch.realmtech.core.helper;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class OnClick extends ClickListener {
    private final OnClickFunction onClickFunction;

    public OnClick(OnClickFunction onClickFunction) {
        this.onClickFunction = onClickFunction;
    }

    public interface OnClickFunction {
        void click(InputEvent event, float x, float y);

    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        onClickFunction.click(event, x, y);
    }
}
