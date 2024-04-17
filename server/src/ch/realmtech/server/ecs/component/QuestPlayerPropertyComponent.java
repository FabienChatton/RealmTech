package ch.realmtech.server.ecs.component;

import ch.realmtech.server.quests.QuestPlayerProperty;
import ch.realmtech.server.registry.QuestEntry;
import com.artemis.Component;

import java.util.List;
import java.util.Optional;

public class QuestPlayerPropertyComponent extends Component {
    private List<QuestPlayerProperty> questPlayerProperties;

    public QuestPlayerPropertyComponent set(List<QuestPlayerProperty> questPlayerProperties) {
        this.questPlayerProperties = questPlayerProperties;
        return this;
    }

    public List<QuestPlayerProperty> getQuestPlayerProperties() {
        return questPlayerProperties;
    }

    public Optional<QuestPlayerProperty> findQuestPlayerProperty(QuestEntry questEntry) {
        return getQuestPlayerProperties().stream().filter((questPlayerProperty) -> questPlayerProperty.getQuestEntry() == questEntry).findFirst();
    }
}
