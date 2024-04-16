package ch.realmtech.server.ecs.system;

import ch.realmtech.server.ecs.component.QuestPlayerPropertyComponent;
import ch.realmtech.server.ecs.plugin.server.SystemsAdminServer;
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

    public long completeQuest(Channel clientChannel, int questEntryId) {
        int playerId = systemsAdminServer.getPlayerManagerServer().getPlayerByChannel(clientChannel);
        QuestEntry questEntry = RegistryUtils.findEntryUnsafe(systemsAdminServer.getRootRegistry(), questEntryId);
        QuestPlayerPropertyComponent questPlayerPropertyComponent = mQuestPlayerProperty.get(playerId);
        QuestPlayerProperty selectedQuestPlayerProperty = questPlayerPropertyComponent.getQuestPlayerProperties().stream().filter(questPlayerProperty -> questPlayerProperty.getQuestEntry() == questEntry).findFirst().orElseThrow();

        long completedTimestamp = System.currentTimeMillis();
        selectedQuestPlayerProperty.setCompleted(completedTimestamp);

        return completedTimestamp;
    }
}
