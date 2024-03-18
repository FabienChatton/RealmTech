package ch.realmtech.server.newMod.options;

import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.newMod.EvaluateBefore;
import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.NewEntry;
import ch.realmtech.server.newRegistry.NewRegistry;
import ch.realmtech.server.newRegistry.OptionEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class OptionLoader extends NewEntry {
    private final static Logger logger = LoggerFactory.getLogger(OptionLoader.class);
    private final Properties propertiesServer;
    private final Properties propertiesClient;
    public OptionLoader() {
        super("Loader");
        propertiesServer = new Properties();
        propertiesClient = new Properties();
    }

    @Override
    @EvaluateBefore("#customOptions")
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        try {
            loadServerProperties();
        } catch (IOException e) {
            throw new InvalideEvaluate("Can not open server properties file. Error: " + e.getMessage());
        }

        try {
            loadClientProperties();
        } catch (IOException e) {
            throw new InvalideEvaluate("Can not open client properties file. Error: " + e.getMessage());
        }
    }

    public void loadServerProperties() throws IOException {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(DataCtrl.getOptionServerFile()))) {
            propertiesServer.load(inputStream);
        }
    }

    public void loadClientProperties() throws IOException {
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(DataCtrl.getOptionFile()))) {
            propertiesClient.load(inputStream);
        }
    }

    public void saveServerOptions() throws IOException {
        try (OutputStream serverOptionFileStream = new BufferedOutputStream(new FileOutputStream(DataCtrl.getOptionServerFile()))) {
            propertiesServer.store(serverOptionFileStream, "RealmTech option server file");
            serverOptionFileStream.flush();
        }
    }

    public void saveClientOption() throws IOException {
        try (OutputStream serverOptionFileStream = new BufferedOutputStream(new FileOutputStream(DataCtrl.getOptionFile()))) {
            propertiesClient.store(serverOptionFileStream, "RealmTech option file");
            serverOptionFileStream.flush();
        }
    }

    public Integer getPropertyOrCreate(Properties properties, String propertyName, Integer propertyDefaultValue) {
        String property = properties.getProperty(propertyName);
        if (property == null) {
            properties.setProperty(propertyName, propertyDefaultValue.toString());
        }
        return property != null ? Integer.parseInt(property) : propertyDefaultValue;
    }

    public Boolean getPropertyOrCreate(Properties properties, String propertyName, Boolean propertyDefaultValue) {
        String property = properties.getProperty(propertyName);
        if (property == null) {
            properties.setProperty(propertyName, propertyDefaultValue.toString());
        }
        return property != null ? Boolean.parseBoolean(property) : propertyDefaultValue;
    }

    public String getPropertyOrCreate(Properties properties, String propertyName, String propertyDefaultValue) {
        String property = properties.getProperty(propertyName);
        if (property == null) {
            properties.setProperty(propertyName, propertyDefaultValue);
        }
        return property != null ? property : propertyDefaultValue;
    }


    public Properties getPropertiesServer() {
        return propertiesServer;
    }

    public Properties getPropertiesClient() {
        return propertiesClient;
    }

    public void setValueBoolean(OptionEntry<Boolean> optionEntry, String value) throws IllegalArgumentException {
        if (value.equals("true")) {
            optionEntry.setValue(true);
        } else if (value.equals("false")) {
            optionEntry.setValue(false);
        } else {
            throw new IllegalArgumentException("Value is not true or false");
        }
    }

    public void setValueInt(OptionEntry<Integer> optionEntry, String value) throws IllegalArgumentException {
        try {
            optionEntry.setValue(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
