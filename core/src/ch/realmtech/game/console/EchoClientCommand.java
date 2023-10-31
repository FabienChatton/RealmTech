package ch.realmtech.game.console;

import ch.realmtechServer.cli.CommandCommunHelper;
import ch.realmtechServer.cli.ConsoleContextOption;
import ch.realmtechServer.packet.serverPacket.ConsoleCommandeRequestPacket;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "echo", description = "echo un message", mixinStandardHelpOptions = true)
public class EchoClientCommand implements Callable<Integer> {
    @ParentCommand
    private MasterClientCommand masterClientCommand;

    @ArgGroup(multiplicity = "1")
    private ConsoleContextOption consoleContextOption;

    @Parameters(description = "Le message a echo")
    private String[] messages;

    @Override
    public Integer call() throws Exception {
        switch (consoleContextOption.getContext()) {
            case CLIENT -> masterClientCommand.output.println(CommandCommunHelper.echo(messages));
            case SERVER -> masterClientCommand.context.getConnexionHandler().sendAndFlushPacketToServer(new ConsoleCommandeRequestPacket(masterClientCommand.stringCommand));
        }
        return 0;
    }

}
