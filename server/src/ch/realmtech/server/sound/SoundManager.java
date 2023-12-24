package ch.realmtech.server.sound;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SoundManager {
    public final static String OPEN_INVENTORY = "sound/effects/inventory/cloth-inventory.wav";
    public final static String FOOT_STEP_GRASS_1 = "sound/effects/level/foot-step/sfx_step_grass_l.mp3";
    public final static String FOOT_STEP_GRASS_2 = "sound/effects/level/foot-step/sfx_step_grass_r.mp3";
    public final static String FOOT_STEP_WATER_1 = "sound/effects/level/foot-step/splash1.wav";
    public final static String FOOT_STEP_SAND_1 = "sound/effects/level/foot-step/Fantozzi-SandR1.ogg";
    public final static String ITEM_PICK_UP = "sound/effects/inventory/click.mp3";
    public final static String ITEM_DROP = "sound/effects/inventory/pop3.ogg";
    public final static String CELL_BREAK1 = "sound/effects/level/break/click_sound_1.mp3";
    public final static String CELL_BREAK2 = "sound/effects/level/break/click_sound_5.mp3";
    public final static String CLICK_CLICK = "sound/effects/menu/click-click-mono.wav";
    public final static String CLICK_OVER = "sound/effects/menu/menu1.wav";
    public final static String BLIP = "sound/effects/menu/blip1.wav";
    public final static String DENY = "sound/effects/menu/launch_deny1.wav";
    public final static String BREAKING_CELL = "sound/effects/level/break/bfh1_breaking_02.ogg";
    private final HashMap<String, Long> soundLoop;
    private final AssetManager assetManager;
    private final AtomicInteger optionSound;

    public SoundManager(AssetManager assetManager, AtomicInteger optionSound) {
        this.assetManager = assetManager;
        this.optionSound = optionSound;
        soundLoop = new HashMap<>();
        soundLoop.put("footStep", System.currentTimeMillis());
        soundLoop.put("cellBreak", System.currentTimeMillis());
        soundLoop.put("clickOver", System.currentTimeMillis());
        soundLoop.put("breakingCell", System.currentTimeMillis());
    }

    public void playOpenInventory() {
        assetManager.get(OPEN_INVENTORY, Sound.class).play(getSoundPourCent() - 0.5f);
    }

    private float getSoundPourCent() {
        return optionSound.get() / 100f;
    }

    public static void LoadAsset(AssetManager assetManager) {
        assetManager.load(OPEN_INVENTORY, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_1, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_2, Sound.class);
        assetManager.load(FOOT_STEP_WATER_1, Sound.class);
        assetManager.load(FOOT_STEP_SAND_1, Sound.class);
        assetManager.load(ITEM_PICK_UP, Sound.class);
        assetManager.load(ITEM_DROP, Sound.class);
        assetManager.load(CELL_BREAK1, Sound.class);
        assetManager.load(CELL_BREAK2, Sound.class);
        assetManager.load(CLICK_CLICK, Sound.class);
        assetManager.load(CLICK_OVER, Sound.class);
        assetManager.load(BLIP, Sound.class);
        assetManager.load(DENY, Sound.class);
        assetManager.load(BREAKING_CELL, Sound.class);
    }

    public void playFootStep(String playerWalkSound, float volume) {
        if (System.currentTimeMillis() - soundLoop.get("footStep") >= 300) {
            soundLoop.put("footStep", System.currentTimeMillis());
            assetManager.get(playerWalkSound, Sound.class).play(getSoundPourCent() * volume);
        }
    }

    public void playItemPickUp() {
        assetManager.get(ITEM_PICK_UP, Sound.class).play(getSoundPourCent());
    }

    public void playItemDrop() {
        assetManager.get(ITEM_DROP, Sound.class).play(getSoundPourCent());
    }

    public void playClickMenu() {
        assetManager.get(CLICK_CLICK, Sound.class).play(getSoundPourCent());
    }

    public void playBlip() {
        assetManager.get(BLIP, Sound.class).play(getSoundPourCent());
    }

    public void playDeny() {
        assetManager.get(DENY, Sound.class).play(getSoundPourCent());
    }

    public void playClickOverMenu() {
        soundLoop.put("clickOver", System.currentTimeMillis());
        assetManager.get(CLICK_OVER, Sound.class).play(getSoundPourCent());
    }

    public boolean playCellBreak() {
        if (System.currentTimeMillis() - soundLoop.get("cellBreak") >= 300) {
            soundLoop.put("cellBreak", System.currentTimeMillis());
            int random = MathUtils.random(0, 1);
            switch (random) {
                case 0 -> assetManager.get(CELL_BREAK1, Sound.class).play(getSoundPourCent());
                case 1 -> assetManager.get(CELL_BREAK2, Sound.class).play(getSoundPourCent());
            }
            return true;
        } else {
            return false;
        }
    }

    public void playBreakingCell() {
        if (System.currentTimeMillis() - soundLoop.get("breakingCell") >= 100) {
            soundLoop.put("breakingCell", System.currentTimeMillis());
            assetManager.get(BREAKING_CELL, Sound.class).play(getSoundPourCent() * 0.1f);
        }
    }
}