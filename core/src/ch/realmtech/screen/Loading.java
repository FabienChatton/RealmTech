package ch.realmtech.screen;

import ch.realmtech.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Loading extends AbstractScreen {
    public Loading(RealmTech context) {
        super(context);
    }

    @Override
    public void clearScreen() {
        Gdx.gl.glClearColor(0, 0.5f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (context.getAssetManager().update()) {
            context.setScreen(ScreenType.MENU);
        }
    }
}
