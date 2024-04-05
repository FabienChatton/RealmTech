package ch.realmtech.server.registry;

import java.util.List;

public class QuestCategory extends Entry {
    private final String displayTitle;
    private final String category;
    private List<QuestEntry> questInThisCategory;
    public QuestCategory(String name, String displayTitle, String category) {
        super(name);
        this.displayTitle = displayTitle;
        this.category = category;
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {
        List<? extends QuestEntry> entries = RegistryUtils.findEntries(rootRegistry, "#quests");
        questInThisCategory = List.copyOf(entries.stream().filter((questEntry) -> questEntry.getCategory().equals(category)).toList());
    }

    public List<QuestEntry> getQuestInThisCategory() {
        return questInThisCategory;
    }

    public String getDisplayTitle() {
        return displayTitle;
    }

    public String getCategory() {
        return category;
    }
}
