package ch.realmtech.server.datactrl;

import ch.realmtech.server.ecs.system.SaveInfManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataCtrl {
    private final static Logger logger = LoggerFactory.getLogger(DataCtrl.class);
    public final static String ROOT_PATH = "RealmTechData";
    public final static String PATH_PROPERTIES = "properties";
    public final static String OPTIONS_FILE = "options.cfg";
    public final static String OPTIONS_SERVER_FILE = "optionsServer.cfg";
    public final static int SCREEN_WIDTH = 1024;
    public final static int SCREEN_HEIGHT = 576;

    public DataCtrl() throws IOException{
        creerHiearchieRealmTechData();
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
        File optionServer = getOptionServerFile();
        if (!optionServer.exists()) {
            Files.createFile(optionServer.toPath());
        }
    }

    public static File getOptionFile() {
        return Path.of(String.format("%s/%s/%s", ROOT_PATH, PATH_PROPERTIES, OPTIONS_FILE)).toFile();
    }

    public static File getOptionServerFile() {
        return Path.of(String.format("%s/%s/%s", ROOT_PATH, PATH_PROPERTIES, OPTIONS_SERVER_FILE)).toFile();
    }
}
