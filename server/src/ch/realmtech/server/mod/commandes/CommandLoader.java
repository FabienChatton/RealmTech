package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.mod.commandes.masterCommand.MasterClientCommandNew;
import ch.realmtech.server.mod.commandes.masterCommand.MasterCommonCommandNew;
import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.registry.*;
import picocli.CommandLine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class CommandLoader extends Entry {
    private HashMap<Class<?>, List<Class<?>>> customCommandChildren;

    public CommandLoader() {
        super("CommandLoader");
    }

    @Override
    // @EvaluateAfter("#customCommands")
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        List<? extends Entry> customCommand = RegistryUtils.findEntries(rootRegistry, "#customCommands");

        customCommandChildren = makeCommandChildren(customCommand);
    }

    private HashMap<Class<?>, List<Class<?>>> makeCommandChildren(List<? extends Entry> commandEntries) throws InvalideEvaluate {
        HashMap<Class<?>, List<Class<?>>> map = new HashMap<>();
        for (Entry customCommand : commandEntries) {
            CommandEntry commandEntry = (CommandEntry) customCommand;
            Class<? extends CommandEntry> commandEntryClass = commandEntry.getClass();
            Class<?> parentCommandClazz = getParentCommandClazz(commandEntry);

            List<Class<?>> children = map.getOrDefault(parentCommandClazz, new ArrayList<>());
            children.add(commandEntryClass);
            map.put(parentCommandClazz, children);

            try {
                new CommandLine(commandEntryClass);
            } catch (CommandLine.InitializationException e) {
                throw new InvalideEvaluate("Invalide initialization for command: " + customCommand.toFqrn() + ". Error: " + e.getMessage());
            }
        }
        return map;
    }

    public Class<?> getParentCommandClazz(CommandEntry commandEntry) throws InvalideEvaluate {
        Class<? extends CommandEntry> commandEntryClass = commandEntry.getClass();
        Field[] declaredFields = commandEntryClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Annotation parrentAnnotation = declaredField.getDeclaredAnnotation(CommandLine.ParentCommand.class);
            if (parrentAnnotation != null) {
                return declaredField.getType();
            }
        }
        throw new InvalideEvaluate("Can not find parent command for command: " + commandEntry);
    }

    public void addServerSubCommand(CommandLine commandLine) {
        List<Class<?>> serverChildren = customCommandChildren.getOrDefault(MasterServerCommandNew.class, List.of());
        List<Class<?>> commonChildren = customCommandChildren.getOrDefault(MasterCommonCommandNew.class, List.of());
        List<Class<?>> children = Stream.concat(serverChildren.stream(), commonChildren.stream()).toList();
        for (Class<?> child : children) {
            addSubCommandRec(child, commandLine);
        }
    }

    public void addClientSubCommand(CommandLine commandLine) {
        List<Class<?>> serverChildren = customCommandChildren.getOrDefault(MasterClientCommandNew.class, List.of());
        List<Class<?>> commonChildren = customCommandChildren.getOrDefault(MasterCommonCommandNew.class, List.of());
        List<Class<?>> children = Stream.concat(serverChildren.stream(), commonChildren.stream()).toList();
        for (Class<?> child : children) {
            addSubCommandRec(child, commandLine);
        }
    }

    public void addSubCommandRec(Class<?> parent, CommandLine commandLine) {
        CommandLine subcommand = new CommandLine(parent);
        commandLine.addSubcommand(subcommand);
        subcommand.setOut(commandLine.getOut());
        subcommand.setErr(commandLine.getErr());
        List<Class<?>> children = customCommandChildren.getOrDefault(parent, List.of());
        for (Class<?> child : children) {
            addSubCommandRec(child, subcommand);
        }
    }
}
