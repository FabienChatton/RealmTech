package ch.realmtech.server.sound;

import ch.realmtech.server.ecs.component.PlayerComponent;
import ch.realmtech.server.mod.options.client.SoundOptionEntry;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;

public class SoundManager {
    public final static String OPEN_INVENTORY = "sound/effects/inventory/cloth-inventory.wav";
    public final static String FOOT_STEP_GRASS_1 = "sound/effects/level/foot-step/grass-0.ogg";
    public final static String FOOT_STEP_GRASS_2 = "sound/effects/level/foot-step/grass-1.ogg";
    public final static String FOOT_STEP_GRASS_3 = "sound/effects/level/foot-step/grass-2.ogg";
    public final static String FOOT_STEP_GRASS_4 = "sound/effects/level/foot-step/grass-3.ogg";
    public final static String FOOT_STEP_GRASS_5 = "sound/effects/level/foot-step/grass-4.ogg";
    public final static String FOOT_STEP_GRASS_6 = "sound/effects/level/foot-step/grass-5.ogg";
    public final static String FOOT_STEP_GRASS_7 = "sound/effects/level/foot-step/grass-6.ogg";
    public final static String FOOT_STEP_GRASS_8 = "sound/effects/level/foot-step/grass-7.ogg";
    public final static String FOOT_STEP_GRASS_9 = "sound/effects/level/foot-step/grass-8.ogg";
    public final static String FOOT_STEP_WATER_1 = "sound/effects/level/foot-step/water-0.ogg";
    public final static String FOOT_STEP_WATER_2 = "sound/effects/level/foot-step/water-1.ogg";
    public final static String FOOT_STEP_WATER_3 = "sound/effects/level/foot-step/water-2.ogg";
    public final static String FOOT_STEP_WATER_4 = "sound/effects/level/foot-step/water-3.ogg";
    public final static String FOOT_STEP_SAND_1 = "sound/effects/level/foot-step/sand-0.ogg";
    public final static String FOOT_STEP_SAND_2 = "sound/effects/level/foot-step/sand-1.ogg";
    public final static String FOOT_STEP_SAND_3 = "sound/effects/level/foot-step/sand-2.ogg";
    public final static String FOOT_STEP_SAND_4 = "sound/effects/level/foot-step/sand-3.ogg";
    public final static String FOOT_STEP_SAND_5 = "sound/effects/level/foot-step/sand-4.ogg";
    public final static String FOOT_STEP_SAND_6 = "sound/effects/level/foot-step/sand-5.ogg";
    public final static String FOOT_STEP_SAND_7 = "sound/effects/level/foot-step/sand-6.ogg";
    public final static String FOOT_STEP_SAND_8 = "sound/effects/level/foot-step/sand-7.ogg";
    public final static String FOOT_STEP_SAND_9 = "sound/effects/level/foot-step/sand-8.ogg";
    public final static String FOOT_STEP_SAND_10 = "sound/effects/level/foot-step/sand-9.ogg";
    public final static String FOOT_STEP_WOOD_1 = "sound/effects/level/foot-step/wood-0.ogg";
    public final static String FOOT_STEP_WOOD_2 = "sound/effects/level/foot-step/wood-1.ogg";
    public final static String FOOT_STEP_WOOD_3 = "sound/effects/level/foot-step/wood-2.ogg";
    public final static String FOOT_STEP_WOOD_4 = "sound/effects/level/foot-step/wood-3.ogg";
    public final static String FOOT_STEP_WOOD_5 = "sound/effects/level/foot-step/wood-4.ogg";
    public final static String FOOT_STEP_WOOD_6 = "sound/effects/level/foot-step/wood-5.ogg";
    public final static String FOOT_STEP_WOOD_7 = "sound/effects/level/foot-step/wood-6.ogg";
    public final static String FOOT_STEP_WOOD_8 = "sound/effects/level/foot-step/wood-7.ogg";
    public final static String FOOT_STEP_WOOD_9 = "sound/effects/level/foot-step/wood-8.ogg";
    public final static String ITEM_PICK_UP = "sound/effects/inventory/click.mp3";
    public final static String ITEM_DROP = "sound/effects/inventory/pop3.ogg";
    public final static String CELL_BREAK1 = "sound/effects/level/break/click_sound_1.mp3";
    public final static String CELL_BREAK2 = "sound/effects/level/break/click_sound_5.mp3";
    public final static String CLICK_CLICK = "sound/effects/menu/click-click-mono.wav";
    public final static String CLICK_OVER = "sound/effects/menu/menu1.wav";
    public final static String BLIP = "sound/effects/menu/blip1.wav";
    public final static String DENY = "sound/effects/menu/launch_deny1.wav";
    public final static String BREAKING_CELL = "sound/effects/level/break/bfh1_breaking_02.ogg";
    public final static String WEAPON_SHOT = "sound/effects/items/bang_05.ogg";
    public final static String SWORD_SWING = "sound/effects/items/Spear_SwingFast2.wav" ;
    private final HashMap<String, Long> soundLoop;
    private final AssetManager assetManager;
    private final SoundOptionEntry optionSound;

    public SoundManager(AssetManager assetManager, SoundOptionEntry optionSound) {
        this.assetManager = assetManager;
        this.optionSound = optionSound;
        soundLoop = new HashMap<>();
        soundLoop.put("cellBreak", System.currentTimeMillis());
        soundLoop.put("clickOver", System.currentTimeMillis());
        soundLoop.put("breakingCell", System.currentTimeMillis());
    }

    public void playOpenInventory() {
        assetManager.get(OPEN_INVENTORY, Sound.class).play(getSoundPourCent() - 0.5f);
    }

    private float getSoundPourCent() {
        return optionSound.getValue() / 100f;
    }

    public static void LoadAsset(AssetManager assetManager) {
        assetManager.load(OPEN_INVENTORY, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_1, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_2, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_3, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_4, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_5, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_6, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_7, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_8, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_9, Sound.class);
        assetManager.load(FOOT_STEP_WATER_1, Sound.class);
        assetManager.load(FOOT_STEP_WATER_2, Sound.class);
        assetManager.load(FOOT_STEP_WATER_3, Sound.class);
        assetManager.load(FOOT_STEP_WATER_4, Sound.class);
        assetManager.load(FOOT_STEP_SAND_1, Sound.class);
        assetManager.load(FOOT_STEP_SAND_2, Sound.class);
        assetManager.load(FOOT_STEP_SAND_3, Sound.class);
        assetManager.load(FOOT_STEP_SAND_4, Sound.class);
        assetManager.load(FOOT_STEP_SAND_5, Sound.class);
        assetManager.load(FOOT_STEP_SAND_6, Sound.class);
        assetManager.load(FOOT_STEP_SAND_7, Sound.class);
        assetManager.load(FOOT_STEP_SAND_8, Sound.class);
        assetManager.load(FOOT_STEP_SAND_9, Sound.class);
        assetManager.load(FOOT_STEP_SAND_10, Sound.class);
        assetManager.load(FOOT_STEP_WOOD_1, Sound.class);
        assetManager.load(FOOT_STEP_WOOD_2, Sound.class);
        assetManager.load(FOOT_STEP_WOOD_3, Sound.class);
        assetManager.load(FOOT_STEP_WOOD_4, Sound.class);
        assetManager.load(FOOT_STEP_WOOD_5, Sound.class);
        assetManager.load(FOOT_STEP_WOOD_6, Sound.class);
        assetManager.load(FOOT_STEP_WOOD_7, Sound.class);
        assetManager.load(FOOT_STEP_WOOD_8, Sound.class);
        assetManager.load(FOOT_STEP_WOOD_9, Sound.class);
        assetManager.load(ITEM_PICK_UP, Sound.class);
        assetManager.load(ITEM_DROP, Sound.class);
        assetManager.load(CELL_BREAK1, Sound.class);
        assetManager.load(CELL_BREAK2, Sound.class);
        assetManager.load(CLICK_CLICK, Sound.class);
        assetManager.load(CLICK_OVER, Sound.class);
        assetManager.load(BLIP, Sound.class);
        assetManager.load(DENY, Sound.class);
        assetManager.load(BREAKING_CELL, Sound.class);
        assetManager.load(WEAPON_SHOT, Sound.class);
        assetManager.load(SWORD_SWING, Sound.class);
    }

    public void playFootStep(int playerId, World world, PlayerFootStepSound playerFootStepSound, float volume) {
        ComponentMapper<PlayerComponent> mPlayer = world.getMapper(PlayerComponent.class);
        HashMap<String, Long> playerSoundLoop = mPlayer.get(playerId).playerSoundLoop;
        if (playerSoundLoop.get("footStep") == null ||
                System.currentTimeMillis() - playerSoundLoop.get("footStep") >= 300) {
            playerSoundLoop.put("footStep", System.currentTimeMillis());
            int randomI = MathUtils.random(0, playerFootStepSound.playerFootStepSound().length - 1);
            assetManager.get(playerFootStepSound.playerFootStepSound()[randomI], Sound.class).play(Math.min(0.5f, getSoundPourCent() * volume));
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

    public void playWeaponShoot() {
        assetManager.get(WEAPON_SHOT, Sound.class).play(getSoundPourCent() * 0.7f);
    }

    public void playSwordSwing() {
        assetManager.get(SWORD_SWING, Sound.class).play(getSoundPourCent() * 0.8f);
    }
}
