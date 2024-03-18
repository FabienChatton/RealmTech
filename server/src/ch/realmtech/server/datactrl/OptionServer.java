package ch.realmtech.server.datactrl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Deprecated
public class OptionServer extends OptionCtrl {
    private final static Logger logger = LoggerFactory.getLogger(OptionServer.class);
    private String authServerBaseUrl;
    private String verifyAccessTokenUrn;
    public final AtomicBoolean verifyAccessToken = new AtomicBoolean();
    public final AtomicInteger renderDistance = new AtomicInteger();
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
        renderDistance.set(6);
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
        optionServer.renderDistance.set(Integer.parseInt(propertiesFile.getProperty("renderDistance")));
        return optionServer;
    }

    public void save() throws IOException {
        properties.put("authServerBaseUrl", authServerBaseUrl);
        properties.put("verifyAccessTokenUrn", verifyAccessTokenUrn);
        properties.put("verifyAccessToken", verifyAccessToken.toString());
        properties.put("renderDistance", renderDistance.toString());
        try (OutputStream outputStream = new FileOutputStream(DataCtrl.getOptionServerFile())) {
            properties.store(outputStream, "RealmTech option server file");
            outputStream.flush();
        }
    }

    public String getAuthServerBaseUrl() {
        return authServerBaseUrl;
    }

    public String getVerifyAccessTokenUrn() {
        return verifyAccessTokenUrn;
    }

    public Optional<String> getOptionValue(String optionName) {
        return switch (optionName) {
            case "authServerBaseUrl" -> Optional.of(getAuthServerBaseUrl());
            case "verifyAccessTokenUrn" -> Optional.of(getVerifyAccessTokenUrn());
            case "verifyAccessToken" -> Optional.of(verifyAccessToken.toString());
            case "renderDistance" -> Optional.of(renderDistance.toString());
            default -> Optional.empty();
        };
    }

    public Map<String, String> listOptions() {
        return new HashMap<>() {
            {
                putListOptions(this, "authServerBaseUrl");
                putListOptions(this, "verifyAccessTokenUrn");
                putListOptions(this, "verifyAccessToken");
                putListOptions(this, "renderDistance");
            }
        };
    }

    @Override
    public void setOptionValue(String optionName, String optionValue) {
        switch (optionName) {
            case "authServerBaseUrl" -> authServerBaseUrl = optionValue;
            case "verifyAccessTokenUrn" -> verifyAccessTokenUrn = optionValue;
            case "verifyAccessToken" -> verifyAccessToken.set(Boolean.parseBoolean(optionValue));
            case "renderDistance" -> renderDistance.set(Integer.parseInt(optionValue));
        }
    }
}
