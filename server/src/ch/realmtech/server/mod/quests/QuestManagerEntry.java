package ch.realmtech.server.mod.quests;

import ch.realmtech.server.mod.EvaluateAfter;
import ch.realmtech.server.quests.QuestPlayerProperty;
import ch.realmtech.server.registry.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QuestManagerEntry extends Entry {
    private List<? extends QuestCategory> questCategories;

    public QuestManagerEntry() {
        super("QuestEntryManager");
    }

    @Override
    @EvaluateAfter("#questsCategory")
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        questCategories = RegistryUtils.findEntries(rootRegistry, "#questsCategory");
    }

    public List<? extends QuestCategory> getQuestCategories() {
        return questCategories;
    }

    public List<? extends QuestEntry> getQuests() {
        return questCategories.stream().map(QuestCategory::getQuestInThisCategory).flatMap(Collection::stream).toList();
    }

    public List<QuestPlayerProperty> mapToQuestEntry(List<QuestPlayerProperty> completedQuestPlayerProperties) {
        List<? extends QuestEntry> allQuests = getQuests();
        List<QuestPlayerProperty> questPlayerProperties = new ArrayList<>(allQuests.size());
        for (QuestEntry questEntry : allQuests) {
            questPlayerProperties.add(completedQuestPlayerProperties.stream()
                    .filter((questPlayerProperty) -> questPlayerProperty.getQuestEntry() == questEntry)
                    .findFirst()
                    .orElse(new QuestPlayerProperty(questEntry)));
        }
        return questPlayerProperties;
    }
}
