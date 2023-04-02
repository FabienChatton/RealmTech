package ch.realmtech.input;

import ch.realmtech.RealmTech;
import ch.realmtech.game.listener.GameCameraListener;
import ch.realmtech.game.listener.GameWorldInputListener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public final class InputMapper implements InputProcessor {
    private final RealmTech context;
    public static KeysMapper moveForward;
    public static KeysMapper moveLeft;
    public static KeysMapper moveRight;
    public static KeysMapper moveBack;
    public static PointerMapper leftClick;
    public static PointerMapper rightClick;
    private final Array<KeysMapper> keysMappers;
    private final Array<PointerMapper> pointerMappers;
    private final Signal<PointerMapper> pointerSignal;
    private final Signal<float[]> scrollSignal;
    private static InputMapper instance;

    private InputMapper(RealmTech context) {
        instance = this;
        this.context = context;
        keysMappers = new Array<>();
        pointerMappers = new Array<>();
        // keys
        moveForward = new KeysMapper(Input.Keys.W);
        moveLeft = new KeysMapper(Input.Keys.A);
        moveRight = new KeysMapper(Input.Keys.D);
        moveBack = new KeysMapper(Input.Keys.S);

        keysMappers.add(moveForward);
        keysMappers.add(moveLeft);
        keysMappers.add(moveRight);
        keysMappers.add(moveBack);

        // pointer
        leftClick = new PointerMapper(0,0);
        rightClick = new PointerMapper(0,1);

        pointerMappers.add(leftClick);
        pointerMappers.add(rightClick);

        // signal pointer
        pointerSignal = new Signal<>();
        pointerSignal.add(new GameWorldInputListener(context));

        // scroll signal
        scrollSignal = new Signal<>();
        scrollSignal.add(new GameCameraListener((OrthographicCamera) context.getGameStage().getCamera()));
    }

    public static InputMapper getInstance(RealmTech context) {
        return instance == null ? new InputMapper(context) : instance;
    }

    public boolean isKeyPressed(int keycode) {
        for (KeysMapper keysMapper : keysMappers) {
            if (keysMapper.key == keycode) {
                return keysMapper.isPressed;
            }
        }
        return false;
    }

    @Override
    public boolean keyDown(final int keycode) {
        boolean ret = false;
        for (KeysMapper keysMapper : keysMappers) {
            if (keysMapper.key == keycode) {
                keysMapper.isPressed = true;
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean ret = false;
        for (KeysMapper keysMapper : keysMappers) {
            if (keysMapper.key == keycode) {
                keysMapper.isPressed = false;
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (PointerMapper pointerMapper : pointerMappers) {
            if (pointerMapper.pointer == pointer && pointerMapper.button == button) {
                pointerMapper.isPressed = true;
                pointerMapper.lastTouchedScreenX = screenX;
                pointerMapper.lastTouchedScreenY = screenY;
                pointerSignal.dispatch(pointerMapper);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (PointerMapper pointerMapper : pointerMappers) {
            if (pointerMapper.pointer == pointer && pointerMapper.button == button) {
                pointerMapper.isPressed = false;
                pointerMapper.lastReleaseScreenX = screenX;
                pointerMapper.lastReleaseScreenY = screenY;
                pointerSignal.dispatch(pointerMapper);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        scrollSignal.dispatch(new float[]{amountX,amountY});
        return false;
    }

    public final static class KeysMapper {
        public int key;
        public boolean isPressed;

        public KeysMapper(int key) {
            this.key = key;
            this.isPressed = false;
        }
    }
    public final static class PointerMapper {
        public int pointer;
        public int button;
        public int lastTouchedScreenX;
        public int lastTouchedScreenY;
        public int lastReleaseScreenX;
        public int lastReleaseScreenY;
        public boolean isPressed;

        public PointerMapper(int pointer, int button) {
            this.pointer = pointer;
            this.button = button;
        }
    }
}