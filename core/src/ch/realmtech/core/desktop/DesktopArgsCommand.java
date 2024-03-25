package ch.realmtech.core.desktop;

import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "desktopArgsCommand", mixinStandardHelpOptions = true)
public class DesktopArgsCommand implements Callable<DesktopConfig> {

    @Option(names = {"-r", "--root-path"}, description = "The root path of the RealmTechData folder. Default in current directory")
    private String rootPath;

    @Override
    public DesktopConfig call() throws Exception {
        return new DesktopConfig(rootPath);
    }
}
