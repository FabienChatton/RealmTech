package ch.realmtech.server.cli;


import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static picocli.CommandLine.*;

@Command(name = "entities", description = "dump all entities in this world.", mixinStandardHelpOptions = true)
public class DumpEntities implements Runnable {
    @ParentCommand
    private DumpCommand dumpCommand;

    @Option(names = {"-f", "--find"}, description = "Class component to find.")
    private List<String> componentFind = new ArrayList<>();
    @Option(names = {"-e", "--exclude"}, description = "exclude component.")
    private List<String> componentExclude = new ArrayList<>();

    @Override
    public void run() {
        StringBuffer sb = new StringBuffer();
        IntBag entities;
        try {
            entities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager()
                    .get(Aspect.one(findComponentsClassByName(componentFind)).exclude(findComponentsClassByName(componentExclude))).getEntities();
        } catch (ClassNotFoundException e) {
            dumpCommand.masterCommand.output.println("Component does not existe: " + e.getMessage());
            return;
        }
        int[] entitiesData = entities.getData();
        dumpCommand.atVerboseLevel(1, () -> {
            for (int i = 0; i < entities.size(); i++) {
                int entityId = entitiesData[i];
                String entityString = dumpCommand.atVerboseLevel(2, () -> {
                    Bag<Component> components = new Bag<>();
                    dumpCommand.masterCommand.getWorld().getEntity(entityId).getComponents(components);
                    Object[] componentsData = components.getData();
                    String componentString = Arrays.stream(componentsData)
                            .limit(components.size())
                            .map(component -> String.format("(%s) %s", component.getClass().getSimpleName(), component))
                            .collect(Collectors.joining(","));
                    return String.format("%d (%d) [%s]", entityId, components.size(), componentString);
                }).orElse(Integer.toString(entityId));
                sb.append(entityString).append("\n");
            }
        });
        if (entities.isEmpty()) dumpCommand.printlnVerbose(0, "no entities");
        else {
            dumpCommand.atVerboseLevel(1, () -> {
                dumpCommand.masterCommand.output.println(
                        dumpCommand.atVerboseLevel(2, () -> "Id | Component count | Components")
                                .orElse("Id")
                );
                dumpCommand.masterCommand.output.println(sb);
            });
            dumpCommand.printlnVerbose(0, "entities count: " + entities.size());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Class<? extends Component>> findComponentsClassByName(List<String> componentClassFinds) throws ClassNotFoundException, ClassCastException {
        List<Class<? extends Component>> componentClassName = new ArrayList<>();
        for (String componentClassFind : componentClassFinds) {
            try {
                // server component
                Class<?> classComponent = Class.forName("ch.realmtech.server.ecs.component." + componentClassFind);
                componentClassName.add((Class<? extends Component>) classComponent);
            } catch (ClassNotFoundException | ClassCastException e) {
                // client component
                Class<?> classComponent = Class.forName("ch.realmtech.core.game.ecs.component." + componentClassFind);
                if (classComponent.isAssignableFrom(Component.class)) {
                    componentClassName.add((Class<? extends Component>) classComponent);
                } else {
                    throw e;
                }
            }
        }
        return componentClassName;
    }
}