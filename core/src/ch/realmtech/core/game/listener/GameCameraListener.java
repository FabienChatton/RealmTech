package ch.realmtech.core.game.listener;

import ch.realmtech.core.observer.Subcriber;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameCameraListener implements Subcriber<float[]> {
    private final static byte MIN_ZOOM = 1;
    private final static byte MAX_ZOOM = 10;
    private final static float SENSIBILITY = 1;
    private final OrthographicCamera gameCamera;

    public GameCameraListener(OrthographicCamera gameCamera) {
        this.gameCamera = gameCamera;
    }

    @Override
    public void receive(float[] zoomAmount) {
        float x = zoomAmount[0];
        float y = zoomAmount[1] / SENSIBILITY;
        if (gameCamera.zoom + y >= MIN_ZOOM && gameCamera.zoom + y <= MAX_ZOOM) {
            gameCamera.zoom += y;
        }
    }
}
