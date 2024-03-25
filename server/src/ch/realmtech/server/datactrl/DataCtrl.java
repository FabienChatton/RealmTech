package ch.realmtech.server.datactrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataCtrl {
    private final static Logger logger = LoggerFactory.getLogger(DataCtrl.class);
    private volatile static String rootPath = "";
    private final static String ROOT_DATA = "RealmTechData";
    private final static String PATH_PROPERTIES = "properties";
    private final static String PLAYERS_PATH = "players";
    private final static String OPTIONS_FILE = "options.cfg";
    private final static String ROOT_PATH_SAVES = "saves";
    private final static String OPTIONS_SERVER_FILE = "optionsServer.cfg";
    private final static String MOD_PATH = "mods";
    public final static int SCREEN_WIDTH = 1024;
    public final static int SCREEN_HEIGHT = 576;

    public static void creerHiearchieRealmTechData(String rootPath) throws IOException {
        if (!rootPath.isEmpty()) {
            DataCtrl.rootPath = rootPath;
            File rootPathFile = Path.of(DataCtrl.rootPath).toFile();
            if (!rootPathFile.exists()) {
                Files.createDirectories(rootPathFile.toPath());
            }
        }
        File rootData = Path.of(String.format("%s/%s", rootPath, ROOT_DATA)).toFile();
        if (!rootData.exists()) {
            Files.createDirectories(rootData.toPath());
        }
        File rootSave = Path.of(String.format("%s/%s/%s", DataCtrl.rootPath, ROOT_DATA, ROOT_PATH_SAVES)).toFile();
        if (!rootSave.exists()) {
            Files.createDirectories(rootSave.toPath());
        }
        File rootProperties = Path.of(String.format("%s/%s/%s", DataCtrl.rootPath, ROOT_DATA, PATH_PROPERTIES)).toFile();
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
        File modDir = getModDir();
        if (!modDir.exists()) {
            Files.createDirectory(modDir.toPath());
        }
    }

    public static String getRootPath() {
        return DataCtrl.rootPath;
    }

    public static File getOptionFile() {
        return Path.of(String.format("%s/%s/%s/%s", rootPath, ROOT_DATA, PATH_PROPERTIES, OPTIONS_FILE)).toFile();
    }

    public static File getOptionServerFile() {
        return Path.of(String.format("%s/%s/%s/%s", rootPath, ROOT_DATA, PATH_PROPERTIES, OPTIONS_SERVER_FILE)).toFile();
    }

    public static File getPlayersDir(String saveName) {
        return Path.of(String.format("%s/%s/%s/%s/%s", rootPath, ROOT_DATA, ROOT_PATH_SAVES, saveName, PLAYERS_PATH)).toFile();
    }

    public static Path getLocalPathSaveRoot() throws IOException {
        return Path.of(String.format("%s/%s/%s", rootPath, ROOT_DATA, ROOT_PATH_SAVES));
    }

    public static File getModDir() {
        return Path.of(String.format("%s/%s/%s", rootPath, ROOT_DATA, MOD_PATH)).toFile();
    }
}
