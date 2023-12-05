package ch.realmtech.server.datactrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class OptionServer {
    private final static Logger logger = LoggerFactory.getLogger(OptionServer.class);
    private String authServerBaseUrl;
    private String verifyAccessTokenUrn;
    public final AtomicBoolean verifyAccessToken = new AtomicBoolean();
    private final Properties properties;

    private OptionServer(Properties properties) {
        this.properties = properties;
    }

    public static OptionServer getOptionFileAndLoadOrCreate() throws IOException {
        try (InputStream inputStream = new FileInputStream(DataCtrl.getOptionServerFile())) {
            Properties propertiesFile = new Properties();
            try {
                propertiesFile.load(inputStream);
                return OptionServer.loadOptionFromFile(propertiesFile);
            } catch (IllegalArgumentException e) {
                return OptionServer.createDefaultOption(propertiesFile);
            }
        }
    }

    private void setDefaultOptionServer() {
        authServerBaseUrl = "https://chattonf01.emf-informatique.ch/RealmTech/auth";
        verifyAccessTokenUrn = "verifyAccessToken.php";
        verifyAccessToken.set(true);
    }

    private static OptionServer loadOptionFromFile(Properties propertiesFile) {
        if (propertiesFile.isEmpty()) {
            throw new IllegalArgumentException("Configuration server file missing");
        }
        return loadFromPropertiesFile(propertiesFile);
    }

    private static OptionServer createDefaultOption(Properties propertiesFile) {
        OptionServer optionServer = new OptionServer(propertiesFile);
        optionServer.setDefaultOptionServer();
        return optionServer;
    }

    private static OptionServer loadFromPropertiesFile(Properties propertiesFile) {
        OptionServer optionServer = new OptionServer(propertiesFile);
        optionServer.authServerBaseUrl = propertiesFile.getProperty("authServerBaseUrl");
        optionServer.verifyAccessTokenUrn = propertiesFile.getProperty("verifyAccessTokenUrn");
        optionServer.verifyAccessToken.set(Boolean.parseBoolean(propertiesFile.getProperty("verifyAccessToken")));
        return optionServer;
    }

    public void saveOptionServer() {
        try {
            properties.put("authServerBaseUrl", authServerBaseUrl);
            properties.put("verifyAccessTokenUrn", verifyAccessTokenUrn);
            properties.put("verifyAccessToken", verifyAccessToken.toString());
            try (OutputStream outputStream = new FileOutputStream(DataCtrl.getOptionServerFile())) {
                properties.store(outputStream, "RealmTech option server file");
                outputStream.flush();
            }
        } catch (IOException e) {
            logger.error("Option file can not be saved. {}", e.getMessage());
        }
    }

    public String getAuthServerBaseUrl() {
        return authServerBaseUrl;
    }

    public String getVerifyAccessTokenUrn() {
        return verifyAccessTokenUrn;
    }
}
