package ch.realmtech.core.option;

import ch.realmtech.server.datactrl.DataCtrl;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ce qui est déclaré sont les options par défaut. Elles sont modifier une fois la lecture du fichier de configuration
 */
public final class Option {
    private final static Logger logger = LoggerFactory.getLogger(Option.class);
    public final AtomicInteger keyMoveUp = new AtomicInteger();
    public final AtomicInteger keyMoveLeft = new AtomicInteger();
    public final AtomicInteger keyMoveRight = new AtomicInteger();
    public final AtomicInteger keyMoveDown = new AtomicInteger();
    public final AtomicInteger openInventory = new AtomicInteger();
    public final AtomicInteger keyDropItem = new AtomicInteger();
    public final BooleanRun fullScreen = new BooleanRun(bool -> {
        if (bool) Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        else Gdx.graphics.setWindowedMode(DataCtrl.SCREEN_WIDTH, DataCtrl.SCREEN_HEIGHT);
    });
    public final IntegerRun fps = new IntegerRun(fps -> Gdx.graphics.setForegroundFPS(fps));
    public final BooleanRun vsync = new BooleanRun(bool -> Gdx.graphics.setVSync(bool));
    public final AtomicBoolean inventoryBlur = new AtomicBoolean();
    public final AtomicInteger sound = new AtomicInteger();
    private final Properties properties;

    private Option(Properties propertiesFile) {
        this.properties = propertiesFile;
    }

    public static Option getOptionFileAndLoadOrCreate() throws IOException {
        InputStream inputStream = new FileInputStream(Path.of(String.format("%s/%s/%s", DataCtrl.ROOT_PATH, DataCtrl.PATH_PROPERTIES, DataCtrl.OPTIONS_FILE)).toFile());
        Properties propertiesFile = new Properties();
        try {
            propertiesFile.load(inputStream);
            return Option.loadOptionFromFile(propertiesFile);
        } catch (IllegalArgumentException e) {
            return Option.createDefaultOption(propertiesFile);
        }
    }

    private static Option createDefaultOption(Properties propertiesFile) {
        Option option = new Option(propertiesFile);
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
        fullScreen.set(false);
        fps.set(60);
        vsync.set(true);
        inventoryBlur.set(true);
        sound.set(100);
    }

    public static void saveOptionFile(final Properties propertiesFile, final Option option) throws IOException {
        propertiesFile.put("keyMoveForward", option.keyMoveUp.toString());
        propertiesFile.put("keyMoveLeft", option.keyMoveLeft.toString());
        propertiesFile.put("keyMoveRight", option.keyMoveRight.toString());
        propertiesFile.put("keyMoveBack", option.keyMoveDown.toString());
        propertiesFile.put("openInventory", option.openInventory.toString());
        propertiesFile.put("keyDropItem", option.keyDropItem.toString());
        propertiesFile.put("fullScreen", option.fullScreen.toString());
        propertiesFile.put("fps", option.fps.toString());
        propertiesFile.put("sound", option.sound.toString());
        propertiesFile.put("vsync", option.vsync.toString());
        propertiesFile.put("inventoryBlur", option.inventoryBlur.toString());
        try (OutputStream outputStream = new FileOutputStream(DataCtrl.getOptionFile())) {
            propertiesFile.store(outputStream, "RealmTech option file");
            outputStream.flush();
        }
    }

    private static Option loadOptionFromFile(Properties propertiesFile) throws IllegalArgumentException {
        if (propertiesFile.isEmpty()) {
            throw new IllegalArgumentException("Configuration file missing");
        }
        return loadFromPropertiesFile(propertiesFile);
    }

    public void saveOption() {
        try {
            Option.saveOptionFile(properties, this);
        } catch (IOException e) {
            logger.error("Option file can not be saved. {}", e.getMessage());
        }
    }

    private static Option loadFromPropertiesFile(Properties propertiesFile) {
        Option option = new Option(propertiesFile);
        option.keyMoveUp.set(Integer.parseInt(propertiesFile.getProperty("keyMoveForward")));
        option.keyMoveLeft.set(Integer.parseInt(propertiesFile.getProperty("keyMoveLeft")));
        option.keyMoveRight.set(Integer.parseInt(propertiesFile.getProperty("keyMoveRight")));
        option.keyMoveDown.set(Integer.parseInt(propertiesFile.getProperty("keyMoveBack")));
        option.openInventory.set(Integer.parseInt(propertiesFile.getProperty("openInventory")));
        option.keyDropItem.set(Integer.parseInt(propertiesFile.getProperty("keyDropItem")));
        option.fullScreen.set(Boolean.parseBoolean(propertiesFile.getProperty("fullScreen")));
        option.fps.set(Integer.parseInt(propertiesFile.getProperty("fps")));
        option.sound.set(Integer.parseInt(propertiesFile.getProperty("sound")));
        option.vsync.set(Boolean.parseBoolean(propertiesFile.getProperty("vsync")));
        option.inventoryBlur.set(Boolean.parseBoolean(propertiesFile.getProperty("inventoryBlur")));
        return option;
    }
}
