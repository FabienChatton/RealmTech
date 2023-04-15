package ch.realmtech.game.ecs.system;

import ch.realmtech.RealmTech;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

public class SoundManager extends Manager {
    @Wire(name = "context")
    RealmTech context;

    public final static String OPEN_INVENTORY = "sound/effects/inventory/cloth-inventory.wav";
    public final static String FOOT_STEP_GRASS_1 = "sound/effects/level/foot-step/sfx_step_grass_l.mp3";
    public final static String FOOT_STEP_GRASS_2 = "sound/effects/level/foot-step/sfx_step_grass_r.mp3";
    public static float soundVolume = 1f;
    private long footStep = 0;

    private AssetManager assetManager;

    @Override
    protected void initialize() {
        super.initialize();
        assetManager = context.getAssetManager();
    }

    public void playOpenInventory() {
        assetManager.get(OPEN_INVENTORY, Sound.class).play(soundVolume - 0.5f);
    }

    public void playFootStepGrass() {
        if (System.currentTimeMillis() - footStep >= 300){
            footStep = System.currentTimeMillis();
            assetManager.get(FOOT_STEP_GRASS_2, Sound.class).play(soundVolume);
        }
    }

    public static void initAsset(AssetManager assetManager) {
        assetManager.load(OPEN_INVENTORY, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_1, Sound.class);
        assetManager.load(FOOT_STEP_GRASS_2, Sound.class);
    }
}
