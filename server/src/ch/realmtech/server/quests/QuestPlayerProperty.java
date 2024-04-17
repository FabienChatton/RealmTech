package ch.realmtech.server.quests;

import ch.realmtech.server.registry.QuestEntry;

public class QuestPlayerProperty {
    private final QuestEntry questEntry;
    private boolean isCompleted;
    private long completedTimestamp;

    /**
     * For completed quests
     */
    public QuestPlayerProperty(QuestEntry questEntry, boolean isCompleted, long completedTimestamp) {
        this.questEntry = questEntry;
        this.isCompleted = isCompleted;
        this.completedTimestamp = completedTimestamp;
    }

    /**
     * For un completed quests
     */
    public QuestPlayerProperty(QuestEntry questEntry) {
        this(questEntry, false, 0);
    }

    public QuestEntry getQuestEntry() {
        return questEntry;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public long getCompletedTimestamp() {
        return completedTimestamp;
    }

    public void setCompleted(long completedTimestamp) {
        this.isCompleted = true;
        this.completedTimestamp = completedTimestamp;
    }
}
