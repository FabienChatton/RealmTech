package ch.realmtech.server.newMod.options;

import ch.realmtech.server.datactrl.DataCtrl;
import ch.realmtech.server.newMod.EvaluateBefore;
import ch.realmtech.server.newRegistry.InvalideEvaluate;
import ch.realmtech.server.newRegistry.NewEntry;
import ch.realmtech.server.newRegistry.NewRegistry;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class OptionLoader extends NewEntry {
    private final Properties propertiesServer;
    public OptionLoader() {
        super("Loader");
        propertiesServer = new Properties();
    }

    @Override
    @EvaluateBefore("#customOptions")
    public void evaluate(NewRegistry<?> rootRegistry) throws InvalideEvaluate {
        try {
            propertiesServer.load(new BufferedInputStream(new FileInputStream(DataCtrl.getOptionServerFile())));
        } catch (IOException e) {
            throw new InvalideEvaluate("Can not open server properties file. Error: " + e.getMessage());
        }

    }


    public Properties getPropertiesServer() {
        return propertiesServer;
    }
}
