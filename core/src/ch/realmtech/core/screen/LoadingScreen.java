package ch.realmtech.core.screen;

import ch.realmtech.core.RealmTech;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class LoadingScreen extends AbstractScreen {
    public LoadingScreen(RealmTech context) {
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
            context.textureAtlasLoaded();
            context.loadingFinish();
        }
    }
}
