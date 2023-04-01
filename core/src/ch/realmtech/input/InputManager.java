package ch.realmtech.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public class InputManager implements InputProcessor {

    public static KeysMapper moveForward;
    public static KeysMapper moveLeft;
    public static KeysMapper moveRight;
    public static KeysMapper moveBack;
    private final Array<KeysMapper> keysMappers;
    private static InputManager instance;

    private InputManager() {
        instance = this;
        keysMappers = new Array<>();
        moveForward = new KeysMapper(Input.Keys.W);
        moveLeft = new KeysMapper(Input.Keys.A);
        moveRight = new KeysMapper(Input.Keys.D);
        moveBack = new KeysMapper(Input.Keys.S);

        keysMappers.add(moveForward);
        keysMappers.add(moveLeft);
        keysMappers.add(moveRight);
        keysMappers.add(moveBack);
    }

    public static InputManager getInstance() {
        return instance == null ? new InputManager() : instance;
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
        /*
        if (keycode == moveForward.key) {
            moveForward.isPressed = true;
            ret = true;
        }
        if (keycode == moveLeft.key) {
            moveLeft.isPressed = true;
            ret = true;
        }
        if (keycode == moveBack.key) {
            moveBack.isPressed = true;
            ret = true;
        }
        if (keycode == moveRight.key) {
            moveRight.isPressed = true;
            ret = true;
        }

         */
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
        /*
        if (keycode == moveForward.key) {
            moveForward.isPressed = false;
            ret = true;
        }
        if (keycode == moveLeft.key) {
            moveLeft.isPressed = false;
            ret = true;
        }
        if (keycode == moveBack.key) {
            moveBack.isPressed = false;
            ret = true;
        }
        if (keycode == moveRight.key) {
            moveRight.isPressed = false;
            ret = true;
        }

         */
        return ret;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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

}













