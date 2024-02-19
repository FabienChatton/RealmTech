package ch.realmtech.server.registery;

public class QuestEntry implements Entry<QuestEntry> {
    private Registry<QuestEntry> registry;
    private final String title;
    private final String content;

    public QuestEntry(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public void setRegistry(Registry<QuestEntry> registry) {
        this.registry = registry;
    }
}
