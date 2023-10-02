package ch.realmtechServer.options;

import ch.realmtechServer.ecs.system.SaveInfManager;
import com.badlogic.gdx.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DataCtrl {
    private final static Logger logger = LoggerFactory.getLogger(DataCtrl.class);
    public final static String ROOT_PATH = "RealmTechData";
    private final static String PATH_PROPERTIES = "properties";
    private final static String OPTIONS_FILE = "options.cfg";
    private final Properties propertiesFile;
    public final Option option;

    public DataCtrl() throws IOException{
        creerHiearchieRealmTechData();
        propertiesFile = new Properties();
        option = loadOptionFile(propertiesFile);
    }

    private Option loadOptionFile(final Properties propertiesFile) throws IOException {
        try {
            final InputStream inputStream = new FileInputStream(Path.of(String.format("%s/%s/%s", ROOT_PATH, PATH_PROPERTIES, OPTIONS_FILE)).toFile());
            propertiesFile.load(inputStream);
            return loadOptionFromFile(propertiesFile);
        } catch (IllegalArgumentException e) {
            return creerDefaultOption(propertiesFile);
        }
    }

    private static Option creerDefaultOption(final Properties propertiesFile) throws IOException {
        final Option option = new Option();
        saveOptionFile(propertiesFile, option);
        logger.info("fichier de configuration créé avec success");
        return option;
    }

    private static void saveOptionFile(final Properties propertiesFile, final Option option) throws IOException {
        propertiesFile.put("keyMoveForward", option.keyMoveForward.toString());
        propertiesFile.put("keyMoveLeft", option.keyMoveLeft.toString());
        propertiesFile.put("keyMoveRight", option.keyMoveRight.toString());
        propertiesFile.put("keyMoveBack", option.keyMoveBack.toString());
        propertiesFile.put("openInventory", option.openInventory.toString());
        propertiesFile.put("renderDistance", option.renderDistance.toString());
        propertiesFile.put("chunkParUpdate", option.chunkParUpdate.toString());
        propertiesFile.put("keyDropItem", option.keyDropItem.toString());
        propertiesFile.put("fullScreen", option.fullScreen.toString());
        propertiesFile.put("fps", option.fps.toString());
        propertiesFile.put("sound", option.sound.toString());
        propertiesFile.put("vsync", option.vsync.toString());
        propertiesFile.put("inventoryBlur", option.inventoryBlur.toString());
        try (OutputStream outputStream = new FileOutputStream(getOptionFile())) {
            propertiesFile.store(outputStream, "le fichier de configuration de RealmTech");
            outputStream.flush();
        }
    }

    private static Option loadOptionFromFile(final Properties propertiesFile) throws IllegalArgumentException {
        if (propertiesFile.size() == 0) {
            final IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Il manque des champs de le fichier de configuration");
            //Gdx.app.log(TAG, illegalArgumentException.getMessage(), illegalArgumentException);
            throw illegalArgumentException;
        }
        final Option option = new Option();
        option.keyMoveForward.set(Integer.parseInt(propertiesFile.getProperty("keyMoveForward")));
        option.keyMoveLeft.set(Integer.parseInt(propertiesFile.getProperty("keyMoveLeft")));
        option.keyMoveRight.set(Integer.parseInt(propertiesFile.getProperty("keyMoveRight")));
        option.keyMoveBack.set(Integer.parseInt(propertiesFile.getProperty("keyMoveBack")));
        option.openInventory.set(Integer.parseInt(propertiesFile.getProperty("openInventory")));
        option.renderDistance.set(Integer.parseInt(propertiesFile.getProperty("renderDistance")));
        option.chunkParUpdate.set(Integer.parseInt(propertiesFile.getProperty("chunkParUpdate")));
        option.keyDropItem.set(Integer.parseInt(propertiesFile.getProperty("keyDropItem")));
        option.fullScreen.set(Boolean.parseBoolean(propertiesFile.getProperty("fullScreen")));
        option.fps.set(Integer.parseInt(propertiesFile.getProperty("fps")));
        option.sound.set(Integer.parseInt(propertiesFile.getProperty("sound")));
        option.vsync.set(Boolean.parseBoolean(propertiesFile.getProperty("vsync")));
        option.inventoryBlur.set(Boolean.parseBoolean(propertiesFile.getProperty("inventoryBlur")));
        return option;
    }

    public void saveConfig() {
        try {
            saveOptionFile(propertiesFile, option);
        } catch (IOException e) {
            //Gdx.app.error(TAG, "La configuration n'a pas put être sauvegarder", e);
        }
    }

    /**
     * Ce qui est déclaré sont les options par défaut. Elles sont modifier une fois la lecture du fichier de configuration
     */
    public static final class Option {
        public final static int RENDER_DISTANCE_MIN = 1;
        public final static int RENDER_DISTANCE_MAX = 16;
        public final AtomicInteger keyMoveForward = new AtomicInteger();
        public final AtomicInteger keyMoveLeft = new AtomicInteger();
        public final AtomicInteger keyMoveRight = new AtomicInteger();
        public final AtomicInteger keyMoveBack = new AtomicInteger();
        public final AtomicInteger openInventory = new AtomicInteger();
        public final AtomicInteger renderDistance = new AtomicInteger();
        public final AtomicInteger chunkParUpdate = new AtomicInteger();
        public final AtomicInteger keyDropItem = new AtomicInteger();
        public final BooleanRun fullScreen = new BooleanRun(bool -> {
            //if (bool) Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            //else Gdx.graphics.setWindowedMode(RealmTech.SCREEN_WIDTH, RealmTech.SCREEN_HEIGHT);
        });
        public final IntegerRun fps = new IntegerRun(fps -> fps.byteValue()/*Gdx.graphics.setForegroundFPS(fps)*/);
        public final BooleanRun vsync = new BooleanRun(bool -> bool.booleanValue() /*Gdx.graphics.setVSync(bool)*/);
        public final AtomicBoolean inventoryBlur = new AtomicBoolean();
        public final AtomicInteger sound = new AtomicInteger();

        {
            setDefaultOption();
        }

        public void setDefaultOption() {
            keyMoveForward.set(Input.Keys.W);
            keyMoveLeft.set(Input.Keys.A);
            keyMoveRight.set(Input.Keys.D);
            keyMoveBack.set(Input.Keys.S);
            openInventory.set(Input.Keys.E);
            renderDistance.set(6);
            chunkParUpdate.set(3);
            keyDropItem.set(Input.Keys.Q);
            fullScreen.set(false);
            fps.set(60);
            vsync.set(true);
            inventoryBlur.set(true);
            sound.set(100);
        }
    }
    public static void creerHiearchieRealmTechData() throws IOException {
        File root = Path.of(ROOT_PATH).toFile();
        if (!root.exists()) {
            Files.createDirectories(root.toPath());
        }
        File rootSave = Path.of(String.format("%s/%s", ROOT_PATH, SaveInfManager.ROOT_PATH_SAVES)).toFile();
        if (!rootSave.exists()) {
            Files.createDirectories(rootSave.toPath());
        }
        File rootProperties = Path.of(String.format("%s/%s", ROOT_PATH, PATH_PROPERTIES)).toFile();
        if (!rootProperties.exists()) {
            Files.createDirectories(rootProperties.toPath());
        }
        File optionFile = getOptionFile();
        if (!optionFile.exists()) {
            Files.createFile(optionFile.toPath());
        }
    }

    private static File getOptionFile() {
        return Path.of(String.format("%s/%s/%s", ROOT_PATH, PATH_PROPERTIES, OPTIONS_FILE)).toFile();
    }
}
