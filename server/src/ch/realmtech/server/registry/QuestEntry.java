package ch.realmtech.server.registry;

public abstract class QuestEntry extends Entry {
    private final String title;
    private final String content;

    public QuestEntry(String name, String title, String content) {
        super(name);
        this.title = title;
        this.content = content;
    }

    @Override
    public void evaluate(Registry<?> rootRegistry) throws InvalideEvaluate {

    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
