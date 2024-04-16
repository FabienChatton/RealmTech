package ch.realmtech.server.ecs.component;

import ch.realmtech.server.quests.QuestPlayerProperty;
import com.artemis.Component;

import java.util.List;

public class QuestPlayerPropertyComponent extends Component {
    private List<QuestPlayerProperty> questPlayerProperties;

    public QuestPlayerPropertyComponent set(List<QuestPlayerProperty> questPlayerProperties) {
        this.questPlayerProperties = questPlayerProperties;
        return this;
    }

    public List<QuestPlayerProperty> getQuestPlayerProperties() {
        return questPlayerProperties;
    }
}
