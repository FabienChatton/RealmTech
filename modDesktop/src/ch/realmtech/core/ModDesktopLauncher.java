package ch.realmtech.core;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import realmtech.mod.Mod;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class ModDesktopLauncher {
    public static void main(String[] arg) throws Exception {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        //config.useVsync(false);
        config.setForegroundFPS(60);
        config.setWindowedMode(RealmTech.SCREEN_WIDTH, RealmTech.SCREEN_HEIGHT);
        config.setWindowIcon(Files.FileType.Internal, "logo/logo-RealmTech-icon.png");
        config.setTitle("RealmTech " + RealmTech.REALMTECH_VERSION);
        Mod mod = new Mod();
        new Lwjgl3Application(new RealmTech(mod, arg), config);
    }
}
