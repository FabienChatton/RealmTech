package ch.realmtech.core;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) throws Exception {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		//config.useVsync(false);
		config.setForegroundFPS(60);
		config.setWindowedMode(RealmTech.SCREEN_WIDTH, RealmTech.SCREEN_HEIGHT);
		config.setWindowIcon(Files.FileType.Internal, "logo/logo-RealmTech-icon.png");
		config.setTitle("RealmTech " + RealmTech.REALMTECH_VERSION);
		new Lwjgl3Application(new RealmTech(arg), config);
	}
}
