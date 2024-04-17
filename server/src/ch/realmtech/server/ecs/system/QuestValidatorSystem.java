package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ServerContext;
import ch.realmtech.server.ecs.component.PlayerConnexionComponent;
import ch.realmtech.server.ecs.component.QuestPlayerPropertyComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.mod.questsValidator.QuestEntryValidator;
import ch.realmtech.server.packet.clientPacket.QuestSetCompleted;
import ch.realmtech.server.quests.QuestPlayerProperty;
import ch.realmtech.server.registry.QuestEntry;
import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;

@All({QuestPlayerPropertyComponent.class, PlayerConnexionComponent.class})
public class QuestValidatorSystem extends IteratingSystem {
    @Wire(name = "serverContext")
    private ServerContext serverContext;
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<QuestPlayerPropertyComponent> mQuestPlayerProperty;
    private ComponentMapper<PlayerConnexionComponent> mPlayerConnexion;

    @Override
    protected void process(int entityId) {
        QuestPlayerPropertyComponent questPlayerPropertyComponent = mQuestPlayerProperty.get(entityId);
        int playerChestInventoryId = systemsAdminServer.getInventoryManager().getChestInventoryId(entityId);
        for (QuestPlayerProperty questPlayerProperty : questPlayerPropertyComponent.getQuestPlayerProperties()) {
            if (!questPlayerProperty.isCompleted()) {
                QuestEntry questEntry = questPlayerProperty.getQuestEntry();
                boolean validate = false;
                for (QuestEntryValidator questEntryValidator : questEntry.getQuestEntryValidators()) {
                    validate = questEntryValidator.validate(systemsAdminServer, playerChestInventoryId);
                    if (!validate) {
                        break;
                    }
                }
                if (validate) {
                    long completedTimestamp = systemsAdminServer.getQuestManagerServer().completeQuest(entityId, questEntry);
                    PlayerConnexionComponent playerConnexionComponent = mPlayerConnexion.get(entityId);
                    serverContext.getServerConnexion().sendPacketTo(new QuestSetCompleted(questEntry.getId(), completedTimestamp), playerConnexionComponent.channel);
                }
            }
        }
    }
}
