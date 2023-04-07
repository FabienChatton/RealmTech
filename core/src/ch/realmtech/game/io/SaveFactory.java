package ch.realmtech.game.io;

import ch.realmtech.RealmTech;

import java.io.File;
import java.io.IOException;

public class SaveFactory {
    public static Save loadSave(File file, RealmTech context) throws IOException {
        Save save = new Save(file, context);
        save.loadMap();
        return save;
    }

    public static Save genereteNewSave(File file, RealmTech context) {
        Save save = new Save(file, context);
        save.genereteNewMap();
        return save;
    }
}
