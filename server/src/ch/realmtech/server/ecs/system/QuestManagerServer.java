package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.QuestPlayerPropertyComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
import ch.realmtech.server.mod.quests.QuestManagerEntry;
import ch.realmtech.server.quests.QuestPlayerProperty;
import ch.realmtech.server.registry.QuestEntry;
import ch.realmtech.server.registry.RegistryUtils;
import com.artemis.ComponentMapper;
import com.artemis.Manager;
import com.artemis.annotations.Wire;
import io.netty.channel.Channel;

public class QuestManagerServer extends Manager {
    @Wire
    private SystemsAdminServer systemsAdminServer;
    private ComponentMapper<QuestPlayerPropertyComponent> mQuestPlayerProperty;
    private QuestManagerEntry questManagerEntry;

    @Override
    protected void initialize() {
        questManagerEntry = RegistryUtils.findEntryOrThrow(systemsAdminServer.getRootRegistry(), QuestManagerEntry.class);
    }

    public long completeQuest(Channel clientChannel, int questEntryId) {
        int playerId = systemsAdminServer.getPlayerManagerServer().getPlayerByChannel(clientChannel);
        QuestEntry questEntry = RegistryUtils.findEntryUnsafe(systemsAdminServer.getRootRegistry(), questEntryId);

        return completeQuest(playerId, questEntry);
    }

    public long completeQuest(int playerId, QuestEntry questEntry) {
        QuestPlayerPropertyComponent questPlayerPropertyComponent = mQuestPlayerProperty.get(playerId);
        QuestPlayerProperty selectedQuestPlayerProperty = questPlayerPropertyComponent.getQuestPlayerProperties().stream().filter(questPlayerProperty -> questPlayerProperty.getQuestEntry() == questEntry).findFirst().orElseThrow();

        long completedTimestamp = System.currentTimeMillis();
        selectedQuestPlayerProperty.setCompleted(completedTimestamp);
        return completedTimestamp;
    }

    public QuestManagerEntry getQuestManagerEntry() {
        return questManagerEntry;
    }
}
