package ch.realmtech.server.mod.commandes;

import ch.realmtech.server.mod.EvaluateAfter;
import ch.realmtech.server.mod.commandes.masterCommand.MasterServerCommandNew;
import ch.realmtech.server.registry.*;
import picocli.CommandLine;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandLoader extends Entry {
    private HashMap<Class<?>, List<Class<?>>> serverCommandChildren;
    private HashMap<Class<?>, List<Class<?>>> clientCommandChildren;

    public CommandLoader() {
        super("CommandLoader");
    }

    @Override
    // @EvaluateAfter("#customCommands")
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        List<? extends Entry> serverCommands = RegistryUtils.findEntries(rootRegistry, "#serverCommands");
        List<? extends Entry> clientCommands = RegistryUtils.findEntries(rootRegistry, "#clientCommands");

        serverCommandChildren = makeCommandChildren(serverCommands);
        clientCommandChildren = makeCommandChildren(clientCommands);
    }

    private HashMap<Class<?>, List<Class<?>>> makeCommandChildren(List<? extends Entry> commandEntries) {
        HashMap<Class<?>, List<Class<?>>> map = new HashMap<>();
        for (Entry customCommand : commandEntries) {
            CommandEntry commandEntry = (CommandEntry) customCommand;
            Class<? extends CommandEntry> commandEntryClass = commandEntry.getClass();
            Class<?> parentCommandClazz = getParentCommandClazz(commandEntry);

            List<Class<?>> children = map.getOrDefault(parentCommandClazz, new ArrayList<>());
            children.add(commandEntryClass);
            map.put(parentCommandClazz, children);
        }
        return map;
    }

    public Class<?> getParentCommandClazz(CommandEntry commandEntry) {
        Class<? extends CommandEntry> commandEntryClass = commandEntry.getClass();
        Field[] declaredFields = commandEntryClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Annotation parrentAnnotation = declaredField.getDeclaredAnnotation(CommandLine.ParentCommand.class);
            if (parrentAnnotation != null) {
                return declaredField.getType();
            }
        }

        try {
            Method evaluate = commandEntryClass.getDeclaredMethod("evaluate", Registry.class);
            EvaluateAfter declaredAnnotation = evaluate.getDeclaredAnnotation(EvaluateAfter.class);
            if (declaredAnnotation != null) {
                return declaredAnnotation.classes()[0];
            }
        } catch (NoSuchMethodException ignored) {
        }
        return null;
    }

    public void addServerSubCommand(Registry<?> registry, CommandLine commandLine) {
        List<Class<?>> children = serverCommandChildren.getOrDefault(MasterServerCommandNew.class, List.of());
        for (Class<?> child : children) {
            addSubCommandRec(child, registry, commandLine);
        }
    }

    public void addClientSubCommand(Registry<?> registry, CommandLine commandLine) {
        List<Class<?>> children = clientCommandChildren.getOrDefault(MasterServerCommandNew.class, List.of());
        for (Class<?> child : children) {
            addSubCommandRec(child, registry, commandLine);
        }
    }

    @SuppressWarnings("unchecked")
    public void addSubCommandRec(Class<?> parent, Registry<?> registry, CommandLine commandLine) {
        CommandLine subcommand = new CommandLine(RegistryUtils.findEntryOrThrow(registry, (Class<? extends CommandEntry>) parent));
        commandLine.addSubcommand(subcommand);
        List<Class<?>> children = serverCommandChildren.getOrDefault(parent, List.of());
        for (Class<?> child : children) {
            addSubCommandRec(child, registry, subcommand);
        }
    }
}
