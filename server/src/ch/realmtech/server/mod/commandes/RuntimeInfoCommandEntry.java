package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.mod.commandes.masterCommand.MasterCommonCommandNew;
import ch.realmtech.server.registry.CommandEntry;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "runtime-info", description = "Show info about the runtime environment")
public class RuntimeInfoCommandEntry extends CommandEntry {
    public RuntimeInfoCommandEntry() {
        super("Info");
    }

    @ParentCommand
    public MasterCommonCommandNew masterCommand;

    @Override
    public void run() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Date uptimeDate = new Date(runtimeMXBean.getUptime());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SS");

        masterCommand.output.println(String.format("OS: %s, %s, %s", System.getProperties().get("os.name"), System.getProperties().get("os.arch"), System.getProperties().get("os.version")));
        masterCommand.output.println(String.format("Uptime (Jvm): %s", dateFormatter.format(uptimeDate)));
        masterCommand.output.println(String.format("Jvm info: %s, %s, (%s)", runtimeMXBean.getVmName(), runtimeMXBean.getVmVersion(), runtimeMXBean.getVmVendor()));
        masterCommand.output.println(String.format("RealmTech Version: %s", ServerContext.REALMTECH_VERSION));
    }
}
