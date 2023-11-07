package ch.realmtech.input;

import ch.realmtech.RealmTech;
import ch.realmtech.game.listener.GameCameraListener;
import ch.realmtech.observer.Observer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;

import java.util.concurrent.atomic.AtomicInteger;

//TODO mettre la gestion des input dans l'ECS avec un composent singleton
public final class InputMapper implements InputProcessor {
    private final RealmTech context;
    public static KeysMapper moveUp;
    public static KeysMapper moveLeft;
    public static KeysMapper moveRight;
    public static KeysMapper moveDown;
    public static KeysMapper openInventory;
    public static PointerMapper leftClick;
    public static PointerMapper rightClick;
    private final Array<KeysMapper> keysMappers;
    private final Array<PointerMapper> pointerMappers;
    private final Observer<float[]> scrollSignal;
    public final Observer<Integer> keysSignal;
    public final Observer<int[]> mouseMove;
    private static InputMapper instance;

    private InputMapper(RealmTech context) {
        instance = this;
        this.context = context;
        keysMappers = new Array<>();
        pointerMappers = new Array<>();
        // keys
        moveUp = new KeysMapper(context.getDataCtrl().option.keyMoveUp);
        moveLeft = new KeysMapper(context.getDataCtrl().option.keyMoveLeft);
        moveRight = new KeysMapper(context.getDataCtrl().option.keyMoveRight);
        moveDown = new KeysMapper(context.getDataCtrl().option.keyMoveDown);
        openInventory = new KeysMapper(context.getDataCtrl().option.openInventory);

        keysMappers.add(moveUp);
        keysMappers.add(moveLeft);
        keysMappers.add(moveRight);
        keysMappers.add(moveDown);
        keysMappers.add(openInventory);

        // pointer
        leftClick = new PointerMapper(0, 0);
        rightClick = new PointerMapper(0, 1);

        pointerMappers.add(leftClick);
        pointerMappers.add(rightClick);


        // scroll signal
        scrollSignal = new Observer<>();
        scrollSignal.add(new GameCameraListener((OrthographicCamera) context.getGameStage().getCamera()));

        // key signal
        keysSignal = new Observer<>();

        // mouse move
        mouseMove = new Observer<>();
    }

    public static InputMapper getInstance(RealmTech context) {
        return instance == null ? new InputMapper(context) : instance;
    }

    public static void reset() {
        moveUp.isPressed = false;
        moveLeft.isPressed = false;
        moveRight.isPressed = false;
        moveDown.isPressed = false;
    }

    public boolean isKeyPressed(final int keycode) {
        for (KeysMapper keysMapper : keysMappers) {
            if (keysMapper.key.get() == keycode) {
                return keysMapper.isPressed;
            }
        }
        return false;
    }

    @Override
    public boolean keyDown(final int keycode) {
        boolean ret = false;
        for (KeysMapper keysMapper : keysMappers) {
            if (keysMapper.key.get() == keycode) {
                keysMapper.isPressed = true;
                ret = true;
            }
        }
        keysSignal.notifySubscribers(keycode);
        return ret;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean ret = false;
        for (KeysMapper keysMapper : keysMappers) {
            if (keysMapper.key.get() == keycode) {
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
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mouseMove.notifySubscribers(new int[]{screenX, screenY});
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        scrollSignal.notifySubscribers(new float[]{amountX, amountY});
        return false;
    }

    public final static class KeysMapper {
        private final AtomicInteger key;
        public boolean isPressed;

        public KeysMapper(AtomicInteger key) {
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