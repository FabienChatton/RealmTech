package ch.realmtech.options;

import ch.realmtech.game.ecs.system.SaveInfManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

public class RealmTechDataCtrl {
    private final static String TAG = RealmTechDataCtrl.class.getSimpleName();
    public final static String ROOT_PATH = "RealmTechData";
    private final static String PATH_PROPERTIES = "properties";
    private final static String OPTIONS_FILE = "options.cfg";
    private final Properties propertiesFile;
    public final Option option;

    public RealmTechDataCtrl() throws IOException{
        creerHiearchieRealmTechData();
        propertiesFile = new Properties();
        option = loadOptionFile(propertiesFile);
    }

    private Option loadOptionFile(final Properties propertiesFile) throws IOException {
        try {
            final InputStream inputStream = Gdx.files.local(String.format("%s/%s/%s", ROOT_PATH, PATH_PROPERTIES, OPTIONS_FILE)).read();
            propertiesFile.load(inputStream);
            return loadOptionFromFile(propertiesFile);
        } catch (IllegalArgumentException e) {
            return creerDefaultOption(propertiesFile);
        }
    }

    private static Option creerDefaultOption(final Properties propertiesFile) throws IOException {
        final Option option = new Option();
        saveOptionFile(propertiesFile, option);
        return option;
    }

    private static void saveOptionFile(final Properties propertiesFile, final Option option) throws IOException {
        propertiesFile.put("keyMoveForward", option.keyMoveForward.toString());
        propertiesFile.put("keyMoveLeft", option.keyMoveLeft.toString());
        propertiesFile.put("keyMoveRight", option.keyMoveRight.toString());
        propertiesFile.put("keyMoveBack", option.keyMoveBack.toString());
        try (OutputStream outputStream = new FileOutputStream(Gdx.files.local(String.format("%s/%s/%s", ROOT_PATH, PATH_PROPERTIES, OPTIONS_FILE)).file())) {
            propertiesFile.store(outputStream, "le fichier de configuration de RealmTech");
            outputStream.flush();
        }
    }

    private static Option loadOptionFromFile(final Properties propertiesFile) throws IllegalArgumentException {
        if (propertiesFile.isEmpty()) throw new IllegalArgumentException("Le fichier de configuration est vide");
        final Option option = new Option();
        option.keyMoveForward.set(Integer.parseInt(propertiesFile.getProperty("keyMoveForward")));
        option.keyMoveLeft.set(Integer.parseInt(propertiesFile.getProperty("keyMoveLeft")));
        option.keyMoveRight.set(Integer.parseInt(propertiesFile.getProperty("keyMoveRight")));
        option.keyMoveBack.set(Integer.parseInt(propertiesFile.getProperty("keyMoveBack")));
        return option;
    }

    public void saveConfig() {
        try {
            saveOptionFile(propertiesFile, option);
        } catch (IOException e) {
            Gdx.app.error(TAG, "La configuration n'a pas put être sauvegarder", e);
        }
    }

    public final static class Option {
        public final AtomicInteger keyMoveForward = new AtomicInteger(Input.Keys.W);
        public final AtomicInteger keyMoveLeft = new AtomicInteger(Input.Keys.A);
        public final AtomicInteger keyMoveRight = new AtomicInteger(Input.Keys.D);
        public final AtomicInteger keyMoveBack = new AtomicInteger(Input.Keys.S);

    }
    public static void creerHiearchieRealmTechData() throws IOException {
        File root = Gdx.files.local(ROOT_PATH).file();
        if (!root.exists()) {
            Files.createDirectories(root.toPath());
        }
        File rootSave = Gdx.files.local(String.format("%s/%s", ROOT_PATH, SaveInfManager.ROOT_PATH_SAVES)).file();
        if (!rootSave.exists()) {
            Files.createDirectories(rootSave.toPath());
        }
        File rootProperties = Gdx.files.local(String.format("%s/%s", ROOT_PATH, PATH_PROPERTIES)).file();
        if (!rootProperties.exists()) {
            Files.createDirectories(rootProperties.toPath());
        }
        File optionFile = Gdx.files.local(String.format("%s/%s/%s", ROOT_PATH, PATH_PROPERTIES, OPTIONS_FILE)).file();
        if (!optionFile.exists()) {
            final boolean newFile = optionFile.createNewFile();
            if (!newFile) throw new IOException("Le fichier " + optionFile.toPath().toFile() + " n'a pas pu être créer");
        }
    }
}
