package ch.realmtech.server.cli;


import ch.realmtech.server.ServerContext;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.ParentCommand;

@Command(name = "runtime-info", aliases = "info", description = "Show info about the runtime environment")
public class RuntimeInfoCommand implements Callable<Integer> {
    @ParentCommand
    private CommunMasterCommand masterCommand;

    @Override
    public Integer call() throws Exception {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Date uptimeDate = new Date(runtimeMXBean.getUptime());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SS");

        masterCommand.output.println(String.format("OS: %s, %s, %s", System.getProperties().get("os.name"), System.getProperties().get("os.arch"), System.getProperties().get("os.version")));
        masterCommand.output.println(String.format("Uptime (Jvm): %s", dateFormatter.format(uptimeDate)));
        masterCommand.output.println(String.format("Jvm info: %s, %s, (%s)", runtimeMXBean.getVmName(), runtimeMXBean.getVmVersion(), runtimeMXBean.getVmVendor()));
        masterCommand.output.println(String.format("RealmTech Version: %s", ServerContext.REALMTECH_VERSION));
        return 0;
    }
}
