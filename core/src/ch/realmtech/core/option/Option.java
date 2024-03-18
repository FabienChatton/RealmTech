package ch.realmtech.core.option;

import ch.realmtech.core.RealmTech;
import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.datactrl.OptionCtrl;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class Option extends OptionCtrl {
    private final RealmTech context;
    public final AtomicInteger keyMoveUp = new AtomicInteger();
    public final AtomicInteger keyMoveLeft = new AtomicInteger();
    public final AtomicInteger keyMoveRight = new AtomicInteger();
    public final AtomicInteger keyMoveDown = new AtomicInteger();
    public final AtomicInteger openInventory = new AtomicInteger();
    public final AtomicInteger keyDropItem = new AtomicInteger();
    public final AtomicInteger keyOpenQuest = new AtomicInteger();
    public final BooleanRun fullScreen = new BooleanRun((bool, context) -> {
        if (bool) Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        else Gdx.graphics.setWindowedMode(DataCtrl.SCREEN_WIDTH, DataCtrl.SCREEN_HEIGHT);
    });
    public final IntegerRun fps = new IntegerRun(fps -> Gdx.graphics.setForegroundFPS(fps));
    public final BooleanRun vsync = new BooleanRun((bool, context) -> Gdx.graphics.setVSync(bool));
    public final AtomicBoolean inventoryBlur = new AtomicBoolean();
    public final AtomicInteger sound = new AtomicInteger();
    private String authServerBaseUrl;
    private String createAccessTokenUrn;
    private String verifyLoginUrn;
    public final BooleanRun tiledTexture = new BooleanRun((bool, context) -> context.getWorldOr((ecsEngine) -> {
        if (!bool) {
            context.getSystemsAdminClient().getTiledTextureSystem().removeAllTiledTextureComponent();
        }
        context.getSystemsAdminClient().getTiledTextureSystem().setEnabled(bool);
    }));
    private final Properties properties;

    private Option(Properties propertiesFile, RealmTech context) {
        this.properties = propertiesFile;
        this.context = context;
    }

    public static Option getOptionFileAndLoadOrCreate(RealmTech context) throws IOException {
        try (InputStream inputStream = new FileInputStream(DataCtrl.getOptionFile())) {
            Properties propertiesFile = new Properties();
            try {
                propertiesFile.load(inputStream);
                return Option.loadOptionFromFile(propertiesFile, context);
            } catch (IllegalArgumentException e) {
                return Option.createDefaultOption(propertiesFile, context);
            }
        }
    }

    private static Option createDefaultOption(Properties propertiesFile, RealmTech context) {
        Option option = new Option(propertiesFile, context);
        option.setDefaultOption();
        return option;
    }

    public void setDefaultOption() {
        keyMoveUp.set(Input.Keys.W);
        keyMoveLeft.set(Input.Keys.A);
        keyMoveRight.set(Input.Keys.D);
        keyMoveDown.set(Input.Keys.S);
        openInventory.set(Input.Keys.E);
        keyDropItem.set(Input.Keys.Q);
        fullScreen.set(false, context);
        fps.set(60);
        vsync.set(true, context);
        inventoryBlur.set(true);
        sound.set(100);
        authServerBaseUrl = "https://chattonf01.emf-informatique.ch/RealmTech/auth";
        createAccessTokenUrn = "createAccessToken.php";
        verifyLoginUrn = "verifyPassword.php";
        tiledTexture.set(false, context);
        keyOpenQuest.set(Input.Keys.C);
    }

    private static Option loadOptionFromFile(Properties propertiesFile, RealmTech context) throws IllegalArgumentException {
        if (propertiesFile.isEmpty()) {
            throw new IllegalArgumentException("Configuration file missing");
        }
        return loadFromPropertiesFile(propertiesFile, context);
    }

    public void save() throws IOException {
        properties.put("keyMoveForward", keyMoveUp.toString());
        properties.put("keyMoveLeft", keyMoveLeft.toString());
        properties.put("keyMoveRight", keyMoveRight.toString());
        properties.put("keyMoveBack", keyMoveDown.toString());
        properties.put("openInventory", openInventory.toString());
        properties.put("keyDropItem", keyDropItem.toString());
        properties.put("fullScreen", fullScreen.toString());
        properties.put("fps", fps.toString());
        properties.put("sound", sound.toString());
        properties.put("vsync", vsync.toString());
        properties.put("inventoryBlur", inventoryBlur.toString());
        properties.put("authServerBaseUrl", authServerBaseUrl);
        properties.put("createAccessTokenUrn", createAccessTokenUrn);
        properties.put("verifyLoginUrn", verifyLoginUrn);
        properties.put("tiledTexture", tiledTexture.toString());
        properties.put("keyOpenQuest", keyOpenQuest.toString());
        try (OutputStream outputStream = new FileOutputStream(DataCtrl.getOptionFile())) {
            properties.store(outputStream, "RealmTech option file");
            outputStream.flush();
        }
    }

    private static Option loadFromPropertiesFile(Properties propertiesFile, RealmTech context) {
        Option option = new Option(propertiesFile, context);
        option.keyMoveUp.set(Integer.parseInt(propertiesFile.getProperty("keyMoveForward")));
        option.keyMoveLeft.set(Integer.parseInt(propertiesFile.getProperty("keyMoveLeft")));
        option.keyMoveRight.set(Integer.parseInt(propertiesFile.getProperty("keyMoveRight")));
        option.keyMoveDown.set(Integer.parseInt(propertiesFile.getProperty("keyMoveBack")));
        option.openInventory.set(Integer.parseInt(propertiesFile.getProperty("openInventory")));
        option.keyDropItem.set(Integer.parseInt(propertiesFile.getProperty("keyDropItem")));
        option.fullScreen.set(Boolean.parseBoolean(propertiesFile.getProperty("fullScreen")), context);
        option.fps.set(Integer.parseInt(propertiesFile.getProperty("fps")));
        option.sound.set(Integer.parseInt(propertiesFile.getProperty("sound")));
        option.vsync.set(Boolean.parseBoolean(propertiesFile.getProperty("vsync")), context);
        option.inventoryBlur.set(Boolean.parseBoolean(propertiesFile.getProperty("inventoryBlur")));
        option.authServerBaseUrl = propertiesFile.getProperty("authServerBaseUrl");
        option.createAccessTokenUrn = propertiesFile.getProperty("createAccessTokenUrn");
        option.verifyLoginUrn = propertiesFile.getProperty("verifyLoginUrn");
        option.tiledTexture.set(Boolean.parseBoolean(propertiesFile.getProperty("tiledTexture")), context);
        option.keyOpenQuest.set(Integer.parseInt(propertiesFile.getProperty("keyOpenQuest")));
        return option;
    }

    public String getAuthServerBaseUrl() {
        return authServerBaseUrl;
    }

    public String getCreateAccessTokenUrn() {
        return createAccessTokenUrn;
    }

    public String getVerifyLoginUrn() {
        return verifyLoginUrn;
    }

    @Override
    public Optional<String> getOptionValue(String optionName) {
        return switch (optionName) {
            case "keyMoveUp" -> Optional.of(Input.Keys.toString(keyMoveUp.get()));
            case "keyMoveLeft" -> Optional.of(Input.Keys.toString(keyMoveLeft.get()));
            case "keyMoveRight" -> Optional.of(Input.Keys.toString(keyMoveRight.get()));
            case "keyMoveDown" -> Optional.of(Input.Keys.toString(keyMoveDown.get()));
            case "openInventory" -> Optional.of(Input.Keys.toString(openInventory.get()));
            case "keyDropItem" -> Optional.of(Input.Keys.toString(keyDropItem.get()));
            case "fullScreen" -> Optional.of(fullScreen.toString());
            case "fps" -> Optional.of(fps.toString());
            case "sound" -> Optional.of(sound.toString());
            case "vsync" -> Optional.of(vsync.toString());
            case "inventoryBlur" -> Optional.of(inventoryBlur.toString());
            case "authServerBaseUrl" -> Optional.of(authServerBaseUrl);
            case "createAccessTokenUrn" -> Optional.of(createAccessTokenUrn);
            case "verifyLoginUrn" -> Optional.of(verifyLoginUrn);
            case "tiledTexture" -> Optional.of(tiledTexture.toString());
            case "keyOpenQuest" -> Optional.of(keyOpenQuest.toString());
            default -> Optional.empty();
        };
    }

    @Override
    public void setOptionValue(String optionName, String optionValue) {
        switch (optionName) {
            case "keyMoveUp" -> keyMoveUp.set(Input.Keys.valueOf(optionValue));
            case "keyMoveLeft" -> keyMoveLeft.set(Input.Keys.valueOf(optionValue));
            case "keyMoveRight" -> keyMoveRight.set(Input.Keys.valueOf(optionValue));
            case "keyMoveDown" -> keyMoveDown.set(Input.Keys.valueOf(optionValue));
            case "openInventory" -> openInventory.set(Input.Keys.valueOf(optionValue));
            case "keyDropItem" -> keyDropItem.set(Input.Keys.valueOf(optionValue));
            case "fullScreen" -> fullScreen.set(Boolean.valueOf(optionValue), context);
            case "fps" -> fps.set(Integer.parseInt(optionValue));
            case "sound" -> sound.set(Integer.parseInt(optionValue));
            case "vsync" -> vsync.set(Boolean.parseBoolean(optionValue), context);
            case "inventoryBlur" -> inventoryBlur.set(Boolean.parseBoolean(optionValue));
            case "authServerBaseUrl" -> authServerBaseUrl = optionValue;
            case "createAccessTokenUrn" -> createAccessTokenUrn = optionValue;
            case "verifyLoginUrn" -> verifyLoginUrn = optionValue;
            case "tiledTexture" -> tiledTexture.set(Boolean.parseBoolean(optionValue), context);
            case "keyOpenQuest" -> keyOpenQuest.set(Input.Keys.valueOf(optionValue));
        }
    }

    @Override
    public Map<String, String> listOptions() {
        return new HashMap<>() {
            {
                putListOptions(this,"keyMoveUp");
                putListOptions(this,"keyMoveLeft");
                putListOptions(this,"keyMoveRight");
                putListOptions(this,"keyMoveDown");
                putListOptions(this,"openInventory");
                putListOptions(this,"keyDropItem");
                putListOptions(this,"fullScreen");
                putListOptions(this,"fps");
                putListOptions(this,"sound");
                putListOptions(this,"vsync");
                putListOptions(this,"inventoryBlur");
                putListOptions(this,"authServerBaseUrl");
                putListOptions(this,"createAccessTokenUrn");
                putListOptions(this,"verifyLoginUrn");
                putListOptions(this, "tiledTexture");
                putListOptions(this, "keyOpenQuest");
            }
        };
    }
}
