package ch.realmtech.server.mod.quests;

import ch.realmtech.server.mod.EvaluateAfter;
import ch.realmtech.server.registry.*;

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
        System.out.println(questCategories);
    }

    public List<? extends QuestCategory> getQuestCategories() {
        return questCategories;
    }

    public List<? extends QuestEntry> getQuests() {
        return questCategories.stream().map(QuestCategory::getQuestInThisCategory).flatMap(Collection::stream).toList();
    }
}
