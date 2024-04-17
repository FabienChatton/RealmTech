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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class QuestManagerServer extends Manager {
    private final static Logger logger = LoggerFactory.getLogger(QuestManagerServer.class);
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
        Optional<QuestPlayerProperty> selectedQuestPlayerPropertyOpt = questPlayerPropertyComponent.getQuestPlayerProperties().stream().filter(questPlayerProperty -> questPlayerProperty.getQuestEntry() == questEntry).findFirst();

        long completedTimestamp = 0;
        if (selectedQuestPlayerPropertyOpt.isPresent()) {
            completedTimestamp = System.currentTimeMillis();
            selectedQuestPlayerPropertyOpt.get().setCompleted(completedTimestamp);
        } else {
            logger.warn("Enable complete quest {}. Enable to find quest entry", questEntry);
        }
        return completedTimestamp;
    }

    public QuestManagerEntry getQuestManagerEntry() {
        return questManagerEntry;
    }
}
