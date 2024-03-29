package ch.realmtech.core.input;

import ch.realmtech.core.RealmTech;
import ch.realmtech.core.game.listener.GameCameraListener;
import ch.realmtech.core.observer.Observer;
import ch.realmtech.server.mod.options.client.KeyOpenQuestOptionEntry;
import ch.realmtech.server.registry.OptionEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;

//TODO mettre la gestion des input dans l'ECS avec un composent singleton
public final class InputMapper implements InputProcessor {
    private final RealmTech context;
    public static KeysMapper moveUp;
    public static KeysMapper moveLeft;
    public static KeysMapper moveRight;
    public static KeysMapper moveDown;
    public static KeysMapper openInventory;
    public static KeysMapper openQuest;
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
        moveUp = new KeysMapper(RegistryUtils.findEntryOrThrow(context.getRootRegistry(), ch.realmtech.server.mod.options.client.KeyMoveUpOptionEntry.class));
        moveLeft = new KeysMapper(RegistryUtils.findEntryOrThrow(context.getRootRegistry(), ch.realmtech.server.mod.options.client.KeyMoveLeftOptionEntry.class));
        moveRight = new KeysMapper(RegistryUtils.findEntryOrThrow(context.getRootRegistry(), ch.realmtech.server.mod.options.client.KeyMoveRightOptionEntry.class));
        moveDown = new KeysMapper(RegistryUtils.findEntryOrThrow(context.getRootRegistry(), ch.realmtech.server.mod.options.client.KeyMoveDownOptionEntry.class));
        openInventory = new KeysMapper(RegistryUtils.findEntryOrThrow(context.getRootRegistry(), ch.realmtech.server.mod.options.client.OpenInventoryOptionEntry.class));
        openQuest = new KeysMapper(RegistryUtils.findEntryOrThrow(context.getRootRegistry(), KeyOpenQuestOptionEntry.class));

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

    public boolean isKeyJustPressed(final int keycode) {
        for (KeysMapper keysMapper : keysMappers) {
            if (keysMapper.key.getValue() == keycode) {
                return keysMapper.isPressed;
            }
        }
        return false;
    }

    @Override
    public boolean keyDown(final int keycode) {
        boolean ret = false;
        for (KeysMapper keysMapper : keysMappers) {
            if (keysMapper.key.getValue() == keycode) {
                keysMapper.isPressed = true;
                keysMapper.isJustPressed = true;
                context.nextFrame(() -> keysMapper.isJustPressed = false);
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
            if (keysMapper.key.getValue() == keycode) {
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
        private final OptionEntry<Integer> key;
        public boolean isPressed;
        public boolean isJustPressed;

        public KeysMapper(OptionEntry<Integer> keyEntry) {
            this.key = keyEntry;
            this.isPressed = false;
        }

        public int getKey() {
            return key.getValue();
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