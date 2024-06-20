package ch.realmtech.server.mod.commandes.dump;

import ch.realmtech.server.ecs.plugin.commun.SystemsAdminCommun;
import ch.realmtech.server.registry.CommandEntry;
import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static picocli.CommandLine.*;

@Command(name = "entities", description = "dump all entities in this world.", mixinStandardHelpOptions = true)
public class DumpEntitiesCommandEntry extends CommandEntry {
    public DumpEntitiesCommandEntry() {
        super("DumpEntity");
    }

    @ParentCommand
    public DumpCommandEntry dumpCommand;

    @Option(names = {"-f", "--find"}, description = "Class component to find.")
    private List<String> componentFind = new ArrayList<>();
    @Option(names = {"-e", "--exclude"}, description = "exclude component.")
    private List<String> componentExclude = new ArrayList<>();

    @Option(names = {"-u", "-uuid"}, description = "Uuid component to find")
    private String uuidFilter;

    @Override
    public void run() {
        StringBuffer sb = new StringBuffer();
        IntBag entities;

        try {
            entities = dumpCommand.masterCommand.getWorld().getAspectSubscriptionManager()
                    .get(Aspect.one(findComponentsClassByName(componentFind).stream().toList())
                            .exclude(findComponentsClassByName(componentExclude))).getEntities();
        } catch (ClassNotFoundException e) {
            dumpCommand.masterCommand.output.println(e.getMessage());
            return;
        }

        IntBag entitiesBag;

        if (uuidFilter != null) {
            entitiesBag = new IntBag();
            SystemsAdminCommun systemsAdminCommun = dumpCommand.masterCommand.getWorld().getRegistered("systemsAdmin");
            int[] entitiesData = entities.getData();
            for (int i = 0; i < entities.size(); i++) {
                int entityId = entitiesData[i];
                UUID entityUuid = systemsAdminCommun.getUuidEntityManager().getEntityUuid(entityId);
                if (entityUuid != null && entityUuid.toString().equals(uuidFilter)) {
                    entitiesBag.add(entityId);
                }
            }
        } else {
            entitiesBag = new IntBag(entities.size());
            int[] entitiesData = entities.getData();
            for (int i = 0; i < entities.size(); i++) {
                int entityId = entitiesData[i];
                entitiesBag.add(entityId);
            }
        }

        dumpCommand.atVerboseLevel(1, () -> {
            for (int i = 0; i < entitiesBag.size(); i++) {
                int entityId = entitiesBag.get(i);
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
        if (entitiesBag.isEmpty()) dumpCommand.printlnVerbose(0, "no entities");
        else {
            dumpCommand.atVerboseLevel(1, () -> {
                dumpCommand.masterCommand.output.println(
                        dumpCommand.atVerboseLevel(2, () -> "Id | Component count | Components")
                                .orElse("Id")
                );
                dumpCommand.masterCommand.output.println(sb);
            });
            dumpCommand.printlnVerbose(0, "entities count: " + entitiesBag.size());
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
                    throw new ClassNotFoundException("The component " + componentClassFind + " doesn't existe");
                }
            }
        }
        return componentClassName;
    }
}
